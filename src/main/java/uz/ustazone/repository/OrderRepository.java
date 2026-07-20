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


    // 1. Category bor, city bor
    @Query("SELECT o.id FROM OrderEntity o " +
            "JOIN o.categories oc " +
            "WHERE o.status = :status " +
            "AND oc.categoryId IN :categoryIds " +
            "AND LOWER(o.city) = LOWER(:city) " +
            "ORDER BY o.createdDate DESC")
    Page<String> findPublicOrderIdsWithCategoriesAndCity(
            @Param("status") OrderStatusEnum status,
            @Param("categoryIds") List<String> categoryIds,
            @Param("city") String city,
            Pageable pageable
    );

    // 2. Category bor, city yo'q
    @Query("SELECT o.id FROM OrderEntity o " +
            "JOIN o.categories oc " +
            "WHERE o.status = :status " +
            "AND oc.categoryId IN :categoryIds " +
            "ORDER BY o.createdDate DESC")
    Page<String> findPublicOrderIdsWithCategoriesNoCity(
            @Param("status") OrderStatusEnum status,
            @Param("categoryIds") List<String> categoryIds,
            Pageable pageable
    );

    // 3. Category yo'q, city yo'q
    @Query("SELECT o.id FROM OrderEntity o " +
            "WHERE o.status = :status " +
            "ORDER BY o.createdDate DESC")
    Page<String> findPublicOrderIdsNoCity(
            @Param("status") OrderStatusEnum status,
            Pageable pageable
    );

    // 4. Category yo'q, city bor
    @Query("SELECT o.id FROM OrderEntity o " +
            "WHERE o.status = :status " +
            "AND LOWER(o.city) = LOWER(:city) " +
            "ORDER BY o.createdDate DESC")
    Page<String> findPublicOrderIdsByCity(
            @Param("status") OrderStatusEnum status,
            @Param("city") String city,
            Pageable pageable
    );

    // Barcha 4 holat uchun umumiy: ID'lar bo'yicha to'liq entity + category'larni fetch qilib olish
    @Query("SELECT DISTINCT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.categories oc " +
            "LEFT JOIN FETCH oc.category c " +
            "WHERE o.id IN :ids " +
            "ORDER BY o.createdDate DESC")
    List<OrderEntity> findByIdsWithCategories(@Param("ids") List<String> ids);

    @Query("SELECT SUM(o.commissionAmount) FROM OrderEntity o")
    BigDecimal sumTotalCommission();
}