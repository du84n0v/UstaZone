package uz.ustazone.service.order;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.category.CategoryShortInfoDTO;
import uz.ustazone.dto.order.OrderCreateDTO;
import uz.ustazone.dto.order.OrderShortResponseDTO;
import uz.ustazone.entity.OrderCategoryEntity;
import uz.ustazone.entity.OrderEntity;
import uz.ustazone.entity.ProfileEntity;
import uz.ustazone.entity.PromoCodeEntity;
import uz.ustazone.enums.OrderStatusEnum;
import uz.ustazone.enums.PromoCodeTypeEnum;
import uz.ustazone.exception.AppBadRequestException;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.*;

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
}