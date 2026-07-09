package uz.ustazone.repository;

import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.MasterWorkDayEntity;

public interface MasterWorkDayRepository extends CrudRepository<MasterWorkDayEntity, String> {
    void deleteAllByMasterProfileId(String masterProfileId);
}
