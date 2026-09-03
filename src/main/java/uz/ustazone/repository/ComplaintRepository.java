package uz.ustazone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uz.ustazone.entity.ComplaintEntity;
import uz.ustazone.enums.ComplaintStatusEnum;
import uz.ustazone.mapper.admin.AdminComplainViewMapper;

import java.util.List;
import java.util.Optional;

public interface ComplaintRepository extends CrudRepository<ComplaintEntity, String> {
    long countByStatus(ComplaintStatusEnum complaintStatusEnum);

    @Query("SELECT " +
            " c.id AS id, " +
            " c.category AS category, " +
            " c.description AS description, " +
            " c.status AS status, " +
            " c.adminNote AS adminNote, " +
            " c.orderId AS orderId, " +
            " r.id AS reporterId, " +
            " r.fullName AS reporterFullName, " +
            " r.phone AS reporterPhone, " +
            " t.id AS targetId, " +
            " t.fullName AS targetFullName, " +
            " t.phone AS targetPhone, " +
            " c.createdDate AS createdDate, " +
            " c.resolvedDate AS resolvedDate " +
            " FROM ComplaintEntity c " +
            " JOIN c.reporter r " +
            " JOIN c.target t " +
            " WHERE c.visible = true " +
            " AND c.status IN :statuses")
    Page<AdminComplainViewMapper> getComplainsByTab(
            @Param("statuses") List<ComplaintStatusEnum> statuses,
            Pageable pageable
    );

    @Query("SELECT " +
            " c.id AS id, " +
            " c.category AS category, " +
            " c.description AS description, " +
            " c.status AS status, " +
            " c.adminNote AS adminNote, " +
            " c.orderId AS orderId, " +
            " r.id AS reporterId, " +
            " r.fullName AS reporterFullName, " +
            " r.phone AS reporterPhone, " +
            " t.id AS targetId, " +
            " t.fullName AS targetFullName, " +
            " t.phone AS targetPhone, " +
            " c.createdDate AS createdDate, " +
            " c.resolvedDate AS resolvedDate " +
            " FROM ComplaintEntity c " +
            " JOIN c.reporter r " +
            " JOIN c.target t " +
            " WHERE c.visible = true " +
            " AND c.id = :id")
    Optional<AdminComplainViewMapper> getMapperById(@Param("id") String id);
}
