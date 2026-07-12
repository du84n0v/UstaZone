package uz.ustazone.dto.profile.master;

import lombok.*;
import uz.ustazone.enums.GeneralStatusEnum;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterCabinetResponseDTO {
    private String id;
    private String fullName;
    private String phone;
    private String email;
    private String avatarUrl;
    private GeneralStatusEnum status;       // ACTIVE, BLOCKED va h.k.
    private Boolean isVerified;  // Hujjatlari tasdiqlanganmi
    private BigDecimal balance;
    private BigDecimal bonusBalance;

    // 2. Ichki obyektlar (Lokatsiya va Kasbiy ma'lumotlar)
    private LocationDTO location;
    private ProfessionalDTO profInfo;

    private Long completedOrdersCount;   // Tamomlagan ishlari soni (status = COMPLETED)
    private Long inProgressOrdersCount;  // Jarayondagi ishlari soni (ACCEPTED, ENROUTE, IN_PROGRESS)
}