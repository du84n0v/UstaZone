package uz.ustazone.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uz.ustazone.entity.ProfileEntity;
import uz.ustazone.enums.GeneralStatusEnum;
import uz.ustazone.enums.ProfileRoleEnum;

import java.util.Optional;

public interface ProfileRepository extends CrudRepository<ProfileEntity, String> {
    Optional<ProfileEntity> findByEmail(String email);

    Optional<ProfileEntity> findByEmailAndPhoneAndVisible(String email, String phone, Boolean visible);

    @Query("SELECT p.avatarAttachId FROM ProfileEntity p WHERE p.id = ?1")
    String findAvatarAttachIdById(String id);

    Optional<ProfileEntity> getById(String profileId);

    Optional<ProfileEntity> findByEmailAndVisibleTrue(String username, boolean visible);

    long countByRole(ProfileRoleEnum profileRoleEnum);

    Optional<ProfileEntity> findByIdAndRole(String id, ProfileRoleEnum role);

    @Query("SELECT p FROM ProfileEntity p " +
            "WHERE p.role = :role " +
            "AND (:search IS NULL OR " +
            "     LOWER(p.fullName) LIKE :search OR " +
            "     p.phone LIKE :search) " +
            "ORDER BY p.createdDate DESC")
    Page<ProfileEntity> findMastersForAdmin(
            @Param("role") ProfileRoleEnum role,
            @Param("search") String search,
            Pageable pageable
    );

    @Transactional
    @Modifying
    @Query("UPDATE ProfileEntity  p SET p.status = ?2 WHERE p.id = ?1" )
    int blockMaster(String masterId, GeneralStatusEnum generalStatusEnum);
}
