package uz.ustazone.service.order;

import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.category.CategoryShortInfoDTO;
import uz.ustazone.dto.order.OrderCreateDTO;
import uz.ustazone.dto.order.OrderShortResponseDTO;
import uz.ustazone.entity.*;
import uz.ustazone.enums.OrderStatusEnum;
import uz.ustazone.enums.PaymentProviderEnum;
import uz.ustazone.enums.PromoCodeTypeEnum;
import uz.ustazone.exception.AppBadRequestException;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.*;
import uz.ustazone.util.SpringSecurityUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final BigDecimal COMMISSION_PERCENT = new BigDecimal("0.05"); // 5% tizim komissiyasi

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderCategoryRepository orderCategoryRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PromoCodeRepository promoCodeRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private SubscriptionRepository subscriptionRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MasterProfileRepository masterProfileRepository;

    @Transactional
    public String createOrder(String clientId, OrderCreateDTO dto) {
        // 1. Mijoz faolligini tekshirish
        ProfileEntity client = profileRepository.findById(clientId)
                .filter(ProfileEntity::getVisible)
                .orElseThrow(() -> new ItemNotFoundException("Mijoz topilmadi"));

        if (Boolean.TRUE.equals(client.getIsBlocked())) {
            throw new AppBadRequestException("Sizning profilingiz bloklangan, buyurtma yarata olmaysiz!");
        }

        BigDecimal originalAmount = dto.getAmount();
        BigDecimal discount = BigDecimal.ZERO;
        String promoCodeId = null;

        // 2. Promokodni tekshirish va chegirma hisoblash
        if (dto.getPromoCodeId() != null && !dto.getPromoCodeId().isBlank()) {
            PromoCodeEntity promo = promoCodeRepository.findById(dto.getPromoCodeId())
                    .filter(PromoCodeEntity::getVisible)
                    .orElseThrow(() -> new ItemNotFoundException("Promokod topilmadi"));

            if (!Boolean.TRUE.equals(promo.getIsActive())) {
                throw new AppBadRequestException("Promokod faol emas!");
            }

            if (promo.getMaxUsageCount() != null && promo.getUsedCount() >= promo.getMaxUsageCount()) {
                throw new AppBadRequestException("Ushbu promokoddan foydalanish limiti tugagan!");
            }

            if (originalAmount.compareTo(BigDecimal.valueOf(promo.getMinOrderAmount())) < 0) {
                throw new AppBadRequestException("Buyurtma summasi promokod minimal miqdoridan kam!");
            }

            if (promo.getType() == PromoCodeTypeEnum.PERCENT) {
                discount = originalAmount.multiply(BigDecimal.valueOf(promo.getDiscountValue()))
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                discount = BigDecimal.valueOf(promo.getDiscountValue());
            }

            discount = discount.min(originalAmount);
            promo.setUsedCount(promo.getUsedCount() + 1);
            promoCodeRepository.save(promo);
            promoCodeId = promo.getId();
        }

        // 3. Moliyaviy qismlarni hisoblash
        BigDecimal finalPayableAmount = originalAmount.subtract(discount);
        BigDecimal commission = finalPayableAmount.multiply(COMMISSION_PERCENT).setScale(2, RoundingMode.HALF_UP);
        BigDecimal masterAmount = finalPayableAmount.subtract(commission);

        // 4. OrderEntity obyektini shakllantirish
        OrderEntity order = OrderEntity.builder()
                .clientId(clientId)
                .title(dto.getTitle().trim())
                .description(dto.getDescription())
                .city(dto.getCity().trim())
                .address(dto.getAddress().trim())
                .clientLat(dto.getClientLat())
                .clientLng(dto.getClientLng())
                .paymentMethod(dto.getPaymentMethod())
                .status(OrderStatusEnum.PENDING)
                .amount(originalAmount)
                .discount(discount)
                .commissionAmount(commission)
                .masterAmount(masterAmount)
                .promoCodeId(promoCodeId)
                .isEmergency(Boolean.TRUE.equals(dto.getIsEmergency()))
                .build();

        order = orderRepository.save(order);

        // 5. Kategoriyalarni bitta batch so'rovda saqlash
        List<OrderCategoryEntity> orderCategories = new ArrayList<>();
        for (String categoryId : dto.getCategoryIds()) {
            if (!categoryRepository.existsById(categoryId)) {
                throw new ItemNotFoundException("Kategoriya topilmadi: " + categoryId);
            }
            OrderCategoryEntity oc = new OrderCategoryEntity();
            oc.setOrderId(order.getId());
            oc.setCategoryId(categoryId);
            orderCategories.add(oc);
        }
        orderCategoryRepository.saveAll(orderCategories);

        // Yangi yaratilgan buyurtma ID si qaytariladi
        return order.getId();
    }

    @Transactional(readOnly = true)
    public Page<OrderShortResponseDTO> getPublicOrders(List<String> categoryIds, String city, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String cityParam = (city != null && !city.isBlank()) ? city.trim() : null;
        boolean hasCategories = categoryIds != null && !categoryIds.isEmpty();
        boolean isFilterApplied = hasCategories || cityParam != null;

        Page<String> idPage;

        // 1. Asosiy filtr so'rovi
        if (hasCategories && cityParam != null) {
            idPage = orderRepository.findPublicOrderIdsWithCategoriesAndCity(
                    OrderStatusEnum.PENDING, categoryIds, cityParam, pageable);
        } else if (hasCategories) {
            idPage = orderRepository.findPublicOrderIdsWithCategoriesNoCity(
                    OrderStatusEnum.PENDING, categoryIds, pageable);
        } else if (cityParam != null) {
            idPage = orderRepository.findPublicOrderIdsByCity(
                    OrderStatusEnum.PENDING, cityParam, pageable);
        } else {
            idPage = orderRepository.findPublicOrderIdsNoCity(
                    OrderStatusEnum.PENDING, pageable);
        }

        List<String> finalIds = new ArrayList<>(idPage.getContent());

        // 2. Agar filtr berilgan bo'lsa va ro'yxat to'lmay qolsa (0 ta yoki size dan kam bo'lsa)
        if (isFilterApplied && finalIds.size() < size) {
            int neededCount = size - finalIds.size();
            Pageable fillPageable = PageRequest.of(0, neededCount);

            List<String> fillIds;
            if (finalIds.isEmpty()) {
                // Umuman topilmagan holat: shunchaki eng yangilaridan neededCount tasini olamiz
                fillIds = orderRepository.findPublicOrderIdsNoCity(
                        OrderStatusEnum.PENDING, fillPageable).getContent();
            } else {
                // Qisman topilgan holat: allaqachon ro'yxatda borlarini chiqarib tashlab to'ldiramiz
                fillIds = orderRepository.findLatestOrderIdsExcluding(
                        OrderStatusEnum.PENDING, finalIds, fillPageable);
            }

            finalIds.addAll(fillIds);
        }

        if (finalIds.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }

        // 3. To'plangan barcha ID lar bo'yicha to'liq entity va kategoriyalarni yuklash
        List<OrderEntity> orders = orderRepository.findByIdsWithCategories(finalIds);

        Map<String, OrderEntity> orderMap = orders.stream()
                .collect(Collectors.toMap(OrderEntity::getId, o -> o));

        List<OrderEntity> orderedOrders = finalIds.stream()
                .map(orderMap::get)
                .filter(Objects::nonNull)
                .toList();

        List<OrderShortResponseDTO> dtos = orderedOrders.stream()
                .map(order -> OrderShortResponseDTO.builder()
                        .id(order.getId())
                        .title(order.getTitle())
                        .amount(order.getAmount())
                        .status(order.getStatus().name())
                        .createdDate(order.getCreatedDate())
                        .region(order.getCity())
                        .address(order.getAddress())
                        .isEmergency(order.getIsEmergency())
                        .categories(order.getCategories() != null
                                ? order.getCategories().stream()
                                  .map(oc -> new CategoryShortInfoDTO(
                                          oc.getCategoryId(),
                                          oc.getCategory() != null ? oc.getCategory().getNameUz() : null))
                                  .toList()
                                : Collections.emptyList())
                        .build())
                .toList();

        // Frontend pagination buzilmasligi uchun jami sonni moslashtiramiz
        long totalElements = Math.max(idPage.getTotalElements(), dtos.size());
        return new PageImpl<>(dtos, pageable, totalElements);
    }

    @Transactional
    public String claimOrder(String masterId, String orderId) {
        // 1. Obunani tekshirish
        SubscriptionEntity subscription = subscriptionRepository.findByUserId(masterId)
                .orElseThrow(() -> new AppBadRequestException("Sizda faol obuna (tarif) yo'q. Iltimos, obuna sotib oling!"));
        // Agar usta FREE (Bepul) tarifda bo'lsa
        if (subscription.getTier() == uz.ustazone.enums.TariffTierEnum.FREE) {
            throw new AppBadRequestException("Bepul (FREE) tarifdagi ustalar buyurtma qabul qila olmaydi. Iltimos, tarifingizni yangilang!");
        }
        // Agar obuna muddati tugagan bo'lsa
        if (subscription.getExpiresDate() != null && subscription.getExpiresDate().isBefore(java.time.LocalDateTime.now())) {
            throw new AppBadRequestException("Obunangiz muddati tugagan. Iltimos, uzaytiring!");
        }
        // 2. Buyurtmani bloklash (Row-level lock) - ikkinchi usta bu qatorda kutib turadi
        OrderEntity order = orderRepository.findByIdForUpdate(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Buyurtma topilmadi"));
        // Holat tekshiruvi: Buyurtma kimdir tomondan olinib bo'linganmi yoki bekor qilinganmi?
        if (order.getStatus() != uz.ustazone.enums.OrderStatusEnum.PENDING) {
            throw new AppBadRequestException("Kechirasiz, bu buyurtma allaqachon boshqa usta tomonidan olingan yoki bekor qilingan.");
        }
        // 3. Buyurtmani ustaga biriktirish
        order.setMasterId(masterId);
        order.setStatus(uz.ustazone.enums.OrderStatusEnum.ACCEPTED);
        order.setAcceptedDate(java.time.LocalDateTime.now());
        orderRepository.save(order);
        return "Buyurtma muvaffaqiyatli qabul qilindi!";
    }

    @Transactional
    public String advanceOrderStatus(String profileId, String orderId, uz.ustazone.enums.OrderStatusEnum newStatus) {
        OrderEntity order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Buyurtma topilmadi"));

        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ItemNotFoundException("Foydalanuvchi topilmadi"));

        String role = profile.getRole().name();
        boolean isClient = profileId.equals(order.getClientId());
        boolean isMaster = order.getMasterId() != null && profileId.equals(order.getMasterId());
        boolean isAdmin = role.equals("ROLE_ADMIN");

        if (!isClient && !isMaster && !isAdmin) {
            throw new AppBadRequestException("Siz bu buyurtmani o'zgartirish huquqiga ega emassiz!");
        }

        uz.ustazone.enums.OrderStatusEnum current = order.getStatus();

        // 1. MIJOZ (CLIENT) HUQUQLARI
        if (isClient) {
            if (newStatus == uz.ustazone.enums.OrderStatusEnum.CANCELLED && current == uz.ustazone.enums.OrderStatusEnum.PENDING) {
                order.setStatus(newStatus);
                order.setCancelledDate(java.time.LocalDateTime.now());
            } else if (newStatus == uz.ustazone.enums.OrderStatusEnum.CONFIRMED && current == uz.ustazone.enums.OrderStatusEnum.COMPLETED) {
                order.setStatus(newStatus);
                order.setClientConfirmed(true);
                uz.ustazone.enums.PaymentProviderEnum provider;
                if (order.getPaymentMethod().name().equals("BONUS")) {
                    provider = PaymentProviderEnum.INTERNAL;
                } else {
                    provider = uz.ustazone.enums.PaymentProviderEnum.valueOf(order.getPaymentMethod().name());
                }
                // 3. Tranzaksiya 1: Mijoz to'lovi (PAYMENT)
                uz.ustazone.entity.TransactionEntity paymentTx = new uz.ustazone.entity.TransactionEntity();
                paymentTx.setUserId(order.getClientId());
                paymentTx.setOrderId(order.getId());
                paymentTx.setType(uz.ustazone.enums.TransactionTypeEnum.PAYMENT);
                paymentTx.setAmount(order.getAmount());
                paymentTx.setStatus(uz.ustazone.enums.TransactionStatusEnum.SUCCESS);
                paymentTx.setProvider(provider);
                transactionRepository.save(paymentTx);
                // 4. Tranzaksiya 2: Tizim komissiyasi (COMMISSION)
                TransactionEntity commissionTx = getTransactionEntity(order);
                transactionRepository.save(commissionTx);
                // 5. Usta balansidan komissiyani ushlab qolish (Yechib olish)
                uz.ustazone.entity.MasterProfileEntity masterProfile = masterProfileRepository.findByProfileId(order.getMasterId())
                        .orElseThrow(() -> new ItemNotFoundException("Usta topilmadi"));

                // Ustaning joriy balansidan komissiyani ayiramiz
                masterProfile.setBalance(masterProfile.getBalance().subtract(order.getCommissionAmount()));

                // Agar to'lov CLICK/PAYME bilan qilingan bo'lsa (Mijoz naqd pul bermasa)
                // U holda ustaning withdrawable_balance (Yechib olish mumkin bo'lgan balansi) ga MasterAmount ni qo'shamiz
                if (provider == uz.ustazone.enums.PaymentProviderEnum.CLICK || provider == uz.ustazone.enums.PaymentProviderEnum.PAYME) {
                    masterProfile.setWithdrawableBalance(masterProfile.getWithdrawableBalance().add(order.getMasterAmount()));
                }

                masterProfileRepository.save(masterProfile);
            } else if (newStatus == uz.ustazone.enums.OrderStatusEnum.DISPUTED) {
                order.setStatus(newStatus);
                order.setIsDispute(true);
            } else {
                throw new AppBadRequestException("Mijoz sifatida '" + current.name() + "' holatidan '" + newStatus.name() + "' ga o'ta olmaysiz!");
            }
        }
        // 2. USTA (MASTER) HUQUQLARI
        else if (isMaster) {
            if (newStatus == uz.ustazone.enums.OrderStatusEnum.EN_ROUTE && current == uz.ustazone.enums.OrderStatusEnum.ACCEPTED) {
                order.setStatus(newStatus);
                order.setEnrouteDate(java.time.LocalDateTime.now());
            } else if (newStatus == uz.ustazone.enums.OrderStatusEnum.IN_PROGRESS && current == uz.ustazone.enums.OrderStatusEnum.EN_ROUTE) {
                order.setStatus(newStatus);
                order.setStartedDate(java.time.LocalDateTime.now());
            } else if (newStatus == uz.ustazone.enums.OrderStatusEnum.COMPLETED && current == uz.ustazone.enums.OrderStatusEnum.IN_PROGRESS) {
                order.setStatus(newStatus);
                order.setCompletedDate(java.time.LocalDateTime.now());
                order.setMasterConfirmed(true);
            } else if (newStatus == uz.ustazone.enums.OrderStatusEnum.DISPUTED) {
                order.setStatus(newStatus);
                order.setIsDispute(true);
            } else {
                throw new AppBadRequestException("Usta sifatida '" + current.name() + "' holatidan '" + newStatus.name() + "' ga o'ta olmaysiz!");
            }
        }
        // 3. ADMIN istalgan holatga o'zgartira oladi (Majburiy aralashuv uchun)
        else {
            order.setStatus(newStatus);
        }

        orderRepository.save(order);

        // TODO: WebSocket orqali Real-Time xabar yuborish (Keyingi qadamda quramiz)

        return "Buyurtma holati " + newStatus.name() + " ga o'zgartirildi!";
    }

    private static @NonNull TransactionEntity getTransactionEntity(OrderEntity order) {
        TransactionEntity commissionTx = new TransactionEntity();
        commissionTx.setUserId(order.getMasterId());
        commissionTx.setOrderId(order.getId());
        commissionTx.setType(uz.ustazone.enums.TransactionTypeEnum.COMMISSION);
        commissionTx.setAmount(order.getCommissionAmount()); // Bu summani createOrder metodida oldindan hisoblab qo'yganmiz
        commissionTx.setStatus(uz.ustazone.enums.TransactionStatusEnum.SUCCESS);
        commissionTx.setProvider(PaymentProviderEnum.INTERNAL);
        return commissionTx;
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public uz.ustazone.dto.order.OrderDetailResponseDTO getOrderDetail(String orderId) {
        uz.ustazone.entity.OrderEntity order = orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new ItemNotFoundException("Buyurtma topilmadi"));

        // Hozirgi so'rov yuborayotgan foydalanuvchini aniqlaymiz (agar token bo'lsa)
        String currentUserId = null;
        boolean isAdmin = false;
        try {
            currentUserId = SpringSecurityUtil.getCurrentProfileId();
            isAdmin = Objects.requireNonNull(SecurityContextHolder.getContext()
                            .getAuthentication()).getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        } catch (Exception ignored) {
        }

        // To'liq ma'lumotni ko'rish huquqi: Egasi (Client), Qabul qilgan usta (Master) yoki Admin
        boolean canSeePrivateInfo = isAdmin || (currentUserId != null &&
                (currentUserId.equals(order.getClientId()) || currentUserId.equals(order.getMasterId())));

        uz.ustazone.dto.order.OrderDetailResponseDTO.OrderDetailResponseDTOBuilder builder = uz.ustazone.dto.order.OrderDetailResponseDTO.builder()
                .id(order.getId())
                .title(order.getTitle())
                .description(order.getDescription())
                .city(order.getCity())
                // Aniq manzil faqat huquqi borlarga ko'rinadi, begonalarga faqat shahar ko'rinadi
                .address(canSeePrivateInfo ? order.getAddress() : order.getCity())
                .status(order.getStatus().name())
                .paymentMethod(order.getPaymentMethod() != null ? order.getPaymentMethod().name() : null)
                .amount(order.getAmount())
                .discount(order.getDiscount())
                .commissionAmount(order.getCommissionAmount())
                .masterAmount(order.getMasterAmount())
                .isEmergency(order.getIsEmergency())
                .isDispute(order.getIsDispute())
                .clientLat(canSeePrivateInfo ? order.getClientLat() : null)
                .clientLng(canSeePrivateInfo ? order.getClientLng() : null)
                .masterLat(order.getMasterLat())
                .masterLng(order.getMasterLng())
                .createdDate(order.getCreatedDate())
                .acceptedDate(order.getAcceptedDate())
                .startedDate(order.getStartedDate())
                .completedDate(order.getCompletedDate())
                .cancelledDate(order.getCancelledDate());

        if (order.getClient() != null) {
            builder.clientFullName(order.getClient().getFullName());
            // Telefon faqat huquqi borlarga ko'rinadi!
            builder.clientPhone(canSeePrivateInfo ? order.getClient().getPhone() : null);
        }

        if (order.getMaster() != null) {
            builder.masterFullName(order.getMaster().getFullName());
            builder.masterPhone(order.getMaster().getPhone());
        }

        if (order.getCategories() != null) {
            builder.categories(order.getCategories().stream()
                    .map(oc -> new uz.ustazone.dto.category.CategoryShortInfoDTO(
                            oc.getCategoryId(),
                            oc.getCategory() != null ? oc.getCategory().getNameUz() : null))
                    .toList());
        }

        return builder.build();
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<OrderShortResponseDTO> getClientOrders(String clientId, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<OrderEntity> orderPage = orderRepository.findByClientId(clientId, pageable);
        return orderPage.map(this::toShortDTO);
    }

    @org.springframework.transaction.annotation.Transactional(readOnly = true)
    public org.springframework.data.domain.Page<OrderShortResponseDTO> getMasterOrders(String masterId, int page, int size) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<OrderEntity> orderPage = orderRepository.findByMasterId(masterId, pageable);
        return orderPage.map(this::toShortDTO);
    }

    private OrderShortResponseDTO toShortDTO(OrderEntity order) {
        return OrderShortResponseDTO.builder()
                .id(order.getId())
                .title(order.getTitle())
                .amount(order.getAmount())
                .status(order.getStatus().name())
                .createdDate(order.getCreatedDate())
                .region(order.getCity())
                .address(order.getAddress())
                .isEmergency(order.getIsEmergency())
                .build();
    }
}