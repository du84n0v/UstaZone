package uz.ustazone.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.ProfileEntity;

import java.util.Optional;

public interface ProfileRepository extends CrudRepository<ProfileEntity, String> {

    Optional<ProfileEntity> findByEmailAndVisibleTrue(String username, Boolean visible);

    Optional<ProfileEntity> findByEmailAndPhoneAndVisible(String email, String phone, Boolean visible);

    Optional<ProfileEntity> findByEmail(String email);

    @Query("SELECT p.avatarAttachId FROM ProfileEntity  p WHERE p.id = ?1")
    String findAvatarAttachIdById(String id);
}
