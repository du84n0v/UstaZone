package uz.ustazone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.ustazone.entity.MasterWorkDayEntity;
import uz.ustazone.enums.WeekDayEnum;
import uz.ustazone.repository.MasterWorkDayRepository;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MasterWorkDayService {

    @Autowired
    private MasterWorkDayRepository masterWorkDayRepository;

    public void merge(String profileId, List<WeekDayEnum> workDays, LocalTime start, LocalTime end) {
        deleteDaysByProfileId(profileId);
        List<MasterWorkDayEntity> ls = new ArrayList<>();
        for (WeekDayEnum workDay : workDays) {
            MasterWorkDayEntity entity = new MasterWorkDayEntity();
            entity.setMasterProfileId(profileId);
            entity.setDayOfWeek(workDay);
            entity.setWorkStart(start);
            entity.setWorkEnd(end);

            ls.add(entity);
        }

        masterWorkDayRepository.saveAll(ls);
    }

    private void deleteDaysByProfileId(String profileId) {
        masterWorkDayRepository.deleteAllByMasterProfileId(profileId);
    }
}
