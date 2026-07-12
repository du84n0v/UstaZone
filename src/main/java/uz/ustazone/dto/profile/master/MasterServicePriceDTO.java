package uz.ustazone.dto.profile.master;

import lombok.*;
import uz.ustazone.enums.PriceTypeEnum;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterServicePriceDTO {
    private String id;                  // master_service (yoki usta va sub-category bog'liqlik) ID'si
    private String subCategoryId;       // Sub-kategoriya ID'si (masalan, Devor bo'yash id-si)
    private String subCategoryName;   // Xizmat nomi (Masalan: "Devor bo'yash", "Dekorativ bo'yoq")
    
    private PriceTypeEnum priceType;  // STARTING, FIXED, HOURLY (Dropdown uchun)
    private BigDecimal minPrice;      // Boshlang'ich narx (so'm) -> FIXED bo'lsa shuni o'zi ishlaydi
    private BigDecimal maxPrice;      // Kelishilgan (maks) narx -> FIXED bo'lsa null bo'ladi
}