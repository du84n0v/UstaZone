package uz.ustazone.dto.profile.master;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class MasterScheduleUpdateDTO {
    private List<MasterWorkDayDTO> days;
}