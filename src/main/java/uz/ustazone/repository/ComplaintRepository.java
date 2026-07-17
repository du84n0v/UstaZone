package uz.ustazone.repository;

import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.ComplaintEntity;
import uz.ustazone.enums.ComplaintStatusEnum;

public interface ComplaintRepository extends CrudRepository<ComplaintEntity, String> {
    long countByStatus(ComplaintStatusEnum complaintStatusEnum);
}
