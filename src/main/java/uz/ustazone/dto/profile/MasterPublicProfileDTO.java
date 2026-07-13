package uz.ustazone.dto.profile;

import lombok.*;
import uz.ustazone.dto.profile.master.LocationDTO;
import uz.ustazone.dto.profile.master.MasterServicePriceDTO;
import uz.ustazone.dto.profile.master.MasterWorkDayDTO;
import uz.ustazone.dto.profile.master.ProfessionalDTO;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterPublicProfileDTO {
    // 1. Shaxsiy va Ishonch ma'lumotlari
    private String id;
    private String fullName;
    private String avatarUrl;
    private Double rating;               // Mijozlar uchun eng muhim fild!
    private Long completedOrdersCount;   // Ustaning tajribasini ko'rsatuvchi karta

    // 2. Lokatsiya va Kasbiy ma'lumotlar
    private LocationDTO location;        // Faqat region va city (Aniq lat/long mijozga buyurtmani qabul qilguncha yashiriladi)
    private ProfessionalDTO profInfo;    // Bio va skills

    // 3. Biz boya yozgan Narxlar va Ish jadvali listlari
    private List<MasterServicePriceDTO> services; // Usta qaysi ishlarni qanchaga qiladi
    private List<MasterWorkDayDTO> schedule;     // Usta qaysi kunlari ishlaydi
}