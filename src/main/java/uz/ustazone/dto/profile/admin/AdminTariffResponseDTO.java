package uz.ustazone.dto.profile.admin;

import lombok.*;
import uz.ustazone.enums.TariffAudienceEnum;
import uz.ustazone.enums.TariffTierEnum;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminTariffResponseDTO {
    private String id;
    private TariffAudienceEnum audience;
    private String tierCode;
    private TariffTierEnum tierName;
    private String icon;
    private String badge;
    private BigDecimal price1Month;
    private BigDecimal price3Month;
    private BigDecimal price6Month;
    private BigDecimal price12Month;
    private String features;
    private Integer sortOrder;
    private Boolean isPopular;
    private Boolean isActive;
}