package uz.ustazone.repository;

import uz.ustazone.entity.AttachEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface AttachRepository extends CrudRepository<AttachEntity,String> {

    @Query("FROM AttachEntity ORDER BY createdDate desc")
    Page<AttachEntity> findAllByOrderByCreatedDateDesc(Pageable pageable);


}
