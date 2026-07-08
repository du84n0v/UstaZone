package uz.ustazone.repository;

import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.ProfileEntity;
import uz.ustazone.enums.GeneralStatusEnum;

import java.util.Optional;

public interface ProfileRepository extends CrudRepository<ProfileEntity, String> {

    Optional<ProfileEntity> findByEmailAndStatusAndVisibleTrue(String email, GeneralStatusEnum status, Boolean visible);
}
