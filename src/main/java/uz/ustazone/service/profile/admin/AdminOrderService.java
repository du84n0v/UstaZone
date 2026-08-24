
package uz.ustazone.service.profile.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.profile.admin.AdminOrderResponseDTO;
import uz.ustazone.entity.OrderEntity;
import uz.ustazone.enums.OrderStatusEnum;
import uz.ustazone.repository.OrderRepository;

import java.math.BigDecimal;

@Service
public class AdminOrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Transactional(readOnly = true)
    public Page<AdminOrderResponseDTO> getOrdersForAdmin(String search, OrderStatusEnum status, Pageable pageable) {
        String searchParam = (search != null && !search.isBlank()) 
                ? "%" + search.trim().toLowerCase() + "%" 
                : null;

        Page<OrderEntity> orderPage = orderRepository.findOrdersForAdmin(status, searchParam, pageable);

        return orderPage.map(order -> {
            // 1. Mijoz nomini aniqlash (agar mavjud bo'lmasa "Noma'lum")
            String clientName = null;
            if (order.getClient() != null && order.getClient().getFullName() != null) {
                clientName = order.getClient().getFullName();
            }
            
            // 2. Usta nomini aniqlash (agar tayinlanmagan bo'lsa "Tayinlanmagan")
            String masterName = null;
            if (order.getMaster() != null && order.getMaster().getFullName() != null) {
                masterName = order.getMaster().getFullName();
            }

            return new AdminOrderResponseDTO(
                    order.getId(),
                    order.getTitle(),
                    clientName,
                    masterName,
                    order.getPaymentMethod(),
                    order.getAmount() != null ? order.getAmount() : BigDecimal.ZERO,
                    order.getCommissionAmount() != null ? order.getCommissionAmount() : BigDecimal.ZERO,
                    order.getStatus(),
                    order.getIsEmergency() != null ? order.getIsEmergency() : false,
                    order.getCreatedDate() != null ? order.getCreatedDate().toLocalDate() : null
            );
        });
    }
}