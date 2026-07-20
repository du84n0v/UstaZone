package uz.ustazone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.category.CategoryShortInfoDTO;
import uz.ustazone.dto.order.OrderCreateDTO;
import uz.ustazone.dto.order.OrderShortResponseDTO;
import uz.ustazone.entity.OrderEntity;
import uz.ustazone.enums.OrderStatusEnum;
import uz.ustazone.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private static final String commissionPercent = "0.05";

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderCategoryService orderCategoryService;

    @Transactional
    public String createOrder(String clientId, OrderCreateDTO dto) {
        // 1. Yangi OrderEntity obyektini ochamiz
        OrderEntity order = new OrderEntity();

        // 2. Mijoz va umumiy ma'lumotlarni set qilamiz
        order.setClientId(clientId);
        order.setTitle(dto.getTitle());
        order.setDescription(dto.getDescription());

        // 3. Lokatsiya ma'lumotlari (2-page)
        order.setCity(dto.getCity());
        order.setAddress(dto.getAddress());
        order.setClientLat(dto.getClientLat());
        order.setClientLng(dto.getClientLng());

        // 4. Moliyaviy va To'lov maydonlari
        order.setAmount(dto.getAmount());
        order.setPaymentMethod(dto.getPaymentMethod());
        if (dto.getPromoCodeId() != null && !dto.getPromoCodeId().isBlank()) {
            order.setPromoCodeId(dto.getPromoCodeId());
        } else {
            order.setPromoCodeId(null);
        }

        // 5. Statuslar va flaglar (Boshlang'ich holat)
        order.setStatus(OrderStatusEnum.PENDING);
        order.setIsEmergency(dto.getIsEmergency() != null ? dto.getIsEmergency() : false);

        // Tizim komissiyasini hisoblash (Masalan: 10% komissiya)
        BigDecimal cPercent = new BigDecimal(commissionPercent);
        BigDecimal commission = dto.getAmount().multiply(cPercent);
        order.setCommissionAmount(commission);
        order.setMasterAmount(dto.getAmount().subtract(commission));

        // 7. Bazaga saqlaymiz. Cascade tufayli order_category'ga ham avtomat yoziladi
        order = orderRepository.save(order);

        for (String categoryId : dto.getCategoryIds()) {
            orderCategoryService.save(categoryId, order.getId());
        }

//         TODO: Shu yerda ushbu kategoriya va lokatsiyaga mos keladigan faol ustalarga
//         TODO: Push Notification (Firebase) yoki WebSocket orqali xabar jo'natish mantiqi chaqiriladi.
        //Hali bularni bilmayman. Organganimdan keyin qilaman

        return "Successfully created";
    }

    @Transactional(readOnly = true)
    public Page<OrderShortResponseDTO> getPublicOrders(List<String> categoryIds, String city, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        String cityParam = (city != null && !city.isBlank()) ? city.trim() : null;
        boolean hasCategories = categoryIds != null && !categoryIds.isEmpty();

        Page<String> idPage;

        if (hasCategories && cityParam != null) {
            idPage = orderRepository.findPublicOrderIdsWithCategoriesAndCity(
                    OrderStatusEnum.PENDING,
                    categoryIds,
                    cityParam,
                    pageable);
        }
        else if (hasCategories) {
            idPage = orderRepository.findPublicOrderIdsWithCategoriesNoCity(
                    OrderStatusEnum.PENDING,
                    categoryIds,
                    pageable);
        }
        else if (cityParam != null) {
            idPage = orderRepository.findPublicOrderIdsByCity(
                    OrderStatusEnum.PENDING,
                    cityParam,
                    pageable);
        } else {
            idPage = orderRepository.findPublicOrderIdsNoCity(
                    OrderStatusEnum.PENDING,
                    pageable);
        }

        List<String> ids = idPage.getContent();
        if (ids.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), pageable, idPage.getTotalElements());
        }

        List<OrderEntity> orders = orderRepository.findByIdsWithCategories(ids);

        // IN so'rovi tartibni kafolatlamaydi, shuning uchun ID tartibiga qaytadan joylashtiramiz
        Map<String, OrderEntity> orderMap = orders.stream()
                .collect(Collectors.toMap(OrderEntity::getId, o -> o));
        List<OrderEntity> orderedOrders = ids.stream()
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
                        .city(order.getCity())
                        .address(order.getCity() != null ? order.getCity() + " tumani" : "")
                        .isEmergency(order.getIsEmergency())
                        .categories(order.getCategories() != null ? order.getCategories().stream()
                                                                    .map(oc -> new CategoryShortInfoDTO(
                                                                            oc.getCategoryId(),
                                                                            oc.getCategory() != null ? oc.getCategory().getNameUz() : null))
                                                                    .toList()
                                : Collections.emptyList())
                        .build())
                .toList();

        return new PageImpl<>(dtos, pageable, idPage.getTotalElements());
    }}
