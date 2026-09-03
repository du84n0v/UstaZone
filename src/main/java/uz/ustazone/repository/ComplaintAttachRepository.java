package uz.ustazone.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uz.ustazone.entity.ComplaintAttachEntity;

import java.util.List;

public interface ComplaintAttachRepository extends CrudRepository<ComplaintAttachEntity, String> {

    // Shikoyatga tegishli barcha fayl ID larini olish
    @Query("SELECT ca.attachId FROM ComplaintAttachEntity ca WHERE ca.complaint.id = :complaintId")
    List<String> findAttachIdsByComplaintId(@Param("complaintId") String complaintId);
}