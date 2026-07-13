package uz.ustazone.repository;

import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.MasterWorkDayEntity;

import java.util.List;

public interface MasterWorkDayRepository extends CrudRepository<MasterWorkDayEntity, String> {
    void deleteAllByMasterProfileId(String masterProfileId);

    List<MasterWorkDayEntity> findAllByMasterProfileId(String masterId);
}
