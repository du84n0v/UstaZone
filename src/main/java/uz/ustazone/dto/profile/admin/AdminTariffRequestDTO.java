package uz.ustazone.dto.profile.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.ustazone.enums.TariffAudienceEnum;

import java.math.BigDecimal;

@Getter
@Setter
public class AdminTariffRequestDTO {

    @NotNull(message = "Auditoriya tanlanishi shart")
    private TariffAudienceEnum audience;

    @NotBlank(message = "Tier (kod) bo'sh bo'lmasligi kerak")
    private String tierCode;

    @NotBlank(message = "Nom kiritilishi shart")
    private String tierName;

    private String icon;
    private String badge;

    @NotNull(message = "1 oylik narx kiritilishi shart")
    private BigDecimal price1Month;

    private BigDecimal price3Month;
    private BigDecimal price6Month;

    @NotNull(message = "12 oylik narx kiritilishi shart")
    private BigDecimal price12Month;

    private String features;

    private Integer sortOrder;
    private Boolean isPopular;
    private Boolean isActive;
}