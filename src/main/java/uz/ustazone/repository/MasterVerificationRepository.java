package uz.ustazone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uz.ustazone.entity.MasterVerificationEntity;
import uz.ustazone.enums.GeneralStatusEnum;
import uz.ustazone.enums.VerificationStatusEnum;
import uz.ustazone.mapper.admin.AdminVerificationMastersMapper;

public interface MasterVerificationRepository extends CrudRepository<MasterVerificationEntity, String> {

    @Query("SELECT mv.id AS id, " +
            " mv.profile.id AS masterId, " +
            " mv.profile.fullName AS fullName, " +
            " mv.profile.phone AS phone, " +
            " mv.status AS status, " +
            " mv.createdDate AS createdDate " +
            " FROM MasterVerificationEntity mv " +
            " INNER JOIN mv.profile p " +
            " WHERE p.visible = true " +
            " AND p.status = :profileStatus " +
            " AND mv.status = :verificationStatus")
    Page<AdminVerificationMastersMapper> getActiveMaster(Pageable pageable,
                                                         @Param("profileStatus") GeneralStatusEnum status,
                                                         @Param("verificationStatus") VerificationStatusEnum verificationStatus);
}
