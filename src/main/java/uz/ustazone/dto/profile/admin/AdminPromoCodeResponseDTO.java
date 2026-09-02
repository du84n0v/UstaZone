package uz.ustazone.dto.profile.admin;

import lombok.*;
import uz.ustazone.enums.PromoCodeTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminPromoCodeResponseDTO {
    private String id;
    private String code;
    private PromoCodeTypeEnum type;
    private Double discountValue;
    private Integer maxUsageCount;
    private Integer usedCount;
    private Long minOrderAmount;
    private Boolean isActive;
    private LocalDateTime createdDate;
}