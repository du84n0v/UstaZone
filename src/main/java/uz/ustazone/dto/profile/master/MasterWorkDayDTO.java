package uz.ustazone.dto.profile.master;

import lombok.Getter;
import lombok.Setter;
import uz.ustazone.enums.WeekDayEnum;

import java.time.LocalTime;

@Getter
@Setter
public class MasterWorkDayDTO {
    private WeekDayEnum dayOfWeek; // MONDAY, TUESDAY ...
    private LocalTime workStart;   // 09:00
    private LocalTime workEnd;     // 18:00
    private Boolean isActive;      // UI-dagi toggle switch holati (agar o'chiq bo'lsa, bazaga qo'shmaymiz yoki o'chirib yuboramiz)
}