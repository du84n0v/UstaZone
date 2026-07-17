package uz.ustazone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uz.ustazone.entity.OrderEntity;
import uz.ustazone.enums.OrderStatusEnum;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public interface OrderRepository extends CrudRepository<OrderEntity, Long> {

    Long countByMasterIdAndStatusIn(String masterId, Collection<OrderStatusEnum> statuses);

    Long countByMasterIdAndStatus(String masterId, OrderStatusEnum status); // COMPLETED status bilan chaqiriladi

    Long countByClientId(String clientId);

    Long countByClientIdAndStatusIn(String clientId, List<OrderStatusEnum> activeStatuses);

    List<OrderEntity> findAllByClientIdAndStatusInOrderByCreatedDateDesc(String clientId, List<OrderStatusEnum> activeStatuses);

    @Query("SELECT DISTINCT o FROM OrderEntity o " +
            "LEFT JOIN o.categories oc " +
            "WHERE o.status = :status " +
            "AND (:categoryIds IS NULL OR oc.categoryId IN :categoryIds) " +
            "AND (:city IS NULL OR LOWER(o.city) = LOWER(:city)) " +
            "ORDER BY o.createdDate DESC")
    Page<OrderEntity> findPublicOrders(
            @Param("status") OrderStatusEnum status,
            @Param("categoryIds") List<String> categoryIds, // List ko'rinishida qabul qiladi
            @Param("city") String city,
            Pageable pageable
    );

    @Query("SELECT SUM(o.commissionAmount) FROM OrderEntity o")
    BigDecimal sumTotalCommission();
}