package uz.ustazone.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderShortResponseDTO {
    
    private String id;               // Buyurtmani bosganda batafsil oynaga (Detail page) o'tish uchun ID
    private String title;            // 3-page: "Zal devorini bo'yash" kabi sarlavha
    private BigDecimal amount;       // Kelishilgan yoki taklif qilingan narx
    private String status;           // PENDING, ACCEPTED, IN_PROGRESS va h.k. (UI rangli badge qo'yadi)
    private LocalDateTime createdDate;// Buyurtma qachon ochilgani (masalan, "2 soat oldin" deb chiqarish uchun)
    
    // --- LOKATSIYA ---
    private String city;             // "Toshkent"
    private String address;          // "Yunusobod tumani, 4-kvartal, 12-uy"

    // --- BOG'LIQ XIZMATLAR ---
    private List<String> categories; // Buyurtma ichidagi tanlangan sub-kategoriyalar nomlari ro'yxati (masalan: ["Devor bo'yash", "Shpaklyovka"])
    
    // --- QO'SHIMCHA FLAGLAR (UI dizayni uchun) ---
    private Boolean isEmergency;     // Agar true bo'lsa, UI-da "SOS" yoki qizil chiroq bilan yorqinroq ajratib ko'rsatiladi
}