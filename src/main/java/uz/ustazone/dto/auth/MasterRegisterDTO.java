package uz.ustazone.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import uz.ustazone.enums.WeekDayEnum;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterRegisterDTO {

    @NotBlank(message = "Ism familiya bo'sh bolishi mumkin emas")
    private String fullName;
    @NotBlank(message = "Telefon raqam bo'sh bolishi mumkin emas")
    private String phone;
    @Email(message = "Email togri formatda bolishi kerak")
    private String email;
    @NotBlank(message = "Parol bo'sh bolishi mumkin emas")
    @Size(min = 5, max = 30, message = "Password uzunligi 5 va 30 orasiga bolishi kerak")
    private String password;

    @NotNull
    private List<String> categoryIds; // Tanlangan aniq xizmatlar (sub-categories) ID ro'yxati

    @NotBlank
    private String region;          // Masalan: "Toshkent shahri"
    @NotBlank
    private String city; // Masalan: "Yunusobod tumani"
    @NotNull
    private Integer serviceRadiusKm; // Slayderdagi kilometr (Masalan: 20)

    @NotNull
    private Double latitude;        // Masalan: 41.311081 (Toshkent koordinatasi)
    @NotNull
    private Double longitude;

    @Positive
    private Integer experienceYear; // Masalan: "1-3 yil", "3-5 yil" (yoki Enum qilsang ham bo'ladi)

    private List<WeekDayEnum> workDays;      // Masalan: [MONDAY, TUESDAY, WEDNESDAY...]
    private LocalTime workStart;             // Masalan: 09:00:00
    private LocalTime workEnd;               // Masalan: 18:00:00
    private Boolean acceptEmergencyCalls;    // Favqulodda chaqiruvlarni qabul qilish (true/false)

    private String bankCardNumber;    // Karta raqami (8600...)
    private String bankAccountNumber; // Bank hisob raqami (2020...) [Ixtiyoriy]

    private String bio; // O'zingiz haqingizda matn

    //##files
    private String passportId;
    private String selfieId;
    private List<String> certificateIds;
    private String profilePicId;
}