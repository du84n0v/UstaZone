package uz.ustazone.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.MasterCertificateEntity;

import java.util.List;

public interface MasterCertificateRepository extends CrudRepository<MasterCertificateEntity, String> {

    void deleteByMasterProfileId(String profileId);

    @Query("SELECT mc.attachId FROM MasterCertificateEntity mc " +
            " WHERE mc.masterProfileId = ?1")
    List<String> getProfileCertificateIds(String profileId);
}
