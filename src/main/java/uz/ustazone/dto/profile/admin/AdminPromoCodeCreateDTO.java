package uz.ustazone.dto.profile.admin;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import uz.ustazone.enums.PromoCodeTypeEnum;

@Getter
@Setter
public class AdminPromoCodeCreateDTO {

    @NotBlank(message = "Promokod kodi kiritilishi shart")
    private String code;

    @NotNull(message = "Promokod turi tanlanishi shart")
    private PromoCodeTypeEnum type;

    @NotNull(message = "Chegirma qiymati kiritilishi shart")
    @Positive(message = "Chegirma qiymati musbat bo'lishi kerak")
    private Double discountValue;

    private Integer maxUsageCount; // Bo'sh kelishi mumkin (cheksiz)

    private Long minOrderAmount; // Min summa (agar kelmasa 0)
}