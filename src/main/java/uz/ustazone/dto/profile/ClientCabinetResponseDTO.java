package uz.ustazone.dto.profile;

import lombok.*;
import uz.ustazone.dto.address.ClientAddressResponseDTO;
import uz.ustazone.dto.order.OrderShortResponseDTO;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientCabinetResponseDTO {
    private String id;
    private String fullName;
    private BigDecimal bonusBalance;

    private Long totalOrdersCount;     // 1-karta: Jami buyurtmalar
    private Long activeOrdersCount;    // 2-karta: Faol buyurtmalar (PENDING, ACCEPTED, ENROUTE, IN_PROGRESS)
    private Long completedOrdersCount; // 3-karta: Bajarilgan (COMPLETED, CONFIRMED, PAID, REVIEWED)

    private List<OrderShortResponseDTO> activeOrders;

    // 2-Tab: Saqlangan manzillar ro'yxati
    private List<ClientAddressResponseDTO> addresses;

    // 3-Tab: Sevimli ustalar ro'yxati (Hozircha bo'sh list ketsa ham bo'ladi)
    private List<String> favoriteMasterIds;
}