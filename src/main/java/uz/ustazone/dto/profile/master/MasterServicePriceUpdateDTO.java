package uz.ustazone.dto.profile.master;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;
import uz.ustazone.enums.PriceTypeEnum;

import java.math.BigDecimal;

@Getter
@Setter
public class MasterServicePriceUpdateDTO {

    @NotNull(message = "Master category ID bo'sh bo'lmasligi kerak")
    private String id; // MasterCategoryEntity jadvalidagi o'sha bog'lanishning unikall ID-si

    @NotNull(message = "Narx turi tanlanishi shart")
    private PriceTypeEnum priceType; // STARTING, FIXED, HOURLY

    @NotNull(message = "Minimal yoki aniq narx kiritilishi shart")
    @PositiveOrZero(message = "Narx 0 dan kichik bo'lishi mumkin emas")
    private BigDecimal minPrice; // Boshlang'ich narx yoki Aniq narx (FIXED bo'lsa)

    @PositiveOrZero(message = "Maksimal narx 0 dan kichik bo'lishi mumkin emas")
    private BigDecimal maxPrice; // Maksimal kelishilgan narx (FIXED bo'lsa null keladi)
}