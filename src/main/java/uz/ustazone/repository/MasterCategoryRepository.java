package uz.ustazone.repository;

import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.MasterCategoryEntity;

public interface MasterCategoryRepository extends CrudRepository<MasterCategoryEntity, String> {
    void deleteByMasterProfileId(String masterProfileId);
}
