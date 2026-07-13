package uz.ustazone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.order.OrderCreateDTO;
import uz.ustazone.entity.OrderCategoryEntity;
import uz.ustazone.entity.OrderEntity;
import uz.ustazone.enums.OrderStatusEnum;
import uz.ustazone.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private static final String commissionPercent = "0.05";

    @Autowired
    private OrderRepository orderRepository;

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
        order.setPromoCodeId(dto.getPromoCodeId());

        // 5. Statuslar va flaglar (Boshlang'ich holat)
        order.setStatus(OrderStatusEnum.PENDING);
        order.setIsEmergency(dto.getIsEmergency() != null ? dto.getIsEmergency() : false);

        // Tizim komissiyasini hisoblash (Masalan: 10% komissiya)
        BigDecimal cPercent = new BigDecimal(commissionPercent);
        BigDecimal commission = dto.getAmount().multiply(cPercent);
        order.setCommissionAmount(commission);
        order.setMasterAmount(dto.getAmount().subtract(commission));

        // 6. Bir nechta kategoriyalarni bog'laymiz (Many-to-Many oraliq jadval)
        List<OrderCategoryEntity> orderCategories = dto.getCategoryIds().stream().map(catId -> {
            OrderCategoryEntity orderCat = new OrderCategoryEntity();
            orderCat.setOrder(order); // Cascade ishlashi uchun ikki tomonlama bog'liqlik shart
            orderCat.setCategoryId(catId);
            return orderCat;
        }).toList();

        // Order ichidagi listga joylaymiz
        order.setCategories(orderCategories);

        // 7. Bazaga saqlaymiz. Cascade tufayli order_category'ga ham avtomat yoziladi
        orderRepository.save(order);

        // TODO: Shu yerda ushbu kategoriya va lokatsiyaga mos keladigan faol ustalarga
        // TODO: Push Notification (Firebase) yoki WebSocket orqali xabar jo'natish mantiqi chaqiriladi.
        //Hali bularni bilmayman. Organganimdan keyin qilaman

        return "Successfully created";
    }
}
