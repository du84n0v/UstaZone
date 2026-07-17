package uz.ustazone.dto.profile.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.ustazone.enums.GeneralStatusEnum;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminMasterResponseDTO {
    private String id;
    private String fullName;
    private String phoneNumber;
    private String city;
    private Double rating;
    private Integer completedJobs;  // Bajarilgan ishlar soni
    private BigDecimal balance;  // Ustaning balansi
    private GeneralStatusEnum status;    // Tasdiqlangan (ACTIVE), Kutilmoqda (PENDING), Rad etilgan (REJECTED/BLOCKED)
}