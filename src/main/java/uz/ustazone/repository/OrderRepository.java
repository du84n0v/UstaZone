package uz.ustazone.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uz.ustazone.entity.OrderEntity;
import uz.ustazone.enums.OrderStatusEnum;
import uz.ustazone.mapper.OrderStatisticsMapper;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends CrudRepository<OrderEntity, String> {

    Long countByMasterIdAndStatusIn(String masterId, Collection<OrderStatusEnum> statuses);

    Long countByMasterIdAndStatus(String masterId, OrderStatusEnum status); // COMPLETED status bilan chaqiriladi

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

    @Query(value = "SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.client " +
            "LEFT JOIN FETCH o.master " +
            "WHERE (:status IS NULL OR o.status = :status) " +
            "AND (:search IS NULL OR LOWER(CAST(o.title AS string)) LIKE LOWER(CAST(:search AS string))) " +
            "ORDER BY o.createdDate DESC",
            countQuery = "SELECT count(o) FROM OrderEntity o " +
                    "WHERE (:status IS NULL OR o.status = :status) " +
                    "AND (:search IS NULL OR LOWER(CAST(o.title AS string)) LIKE LOWER(CAST(:search AS string)))")
    Page<OrderEntity> findOrdersForAdmin(
            @Param("status") OrderStatusEnum status,
            @Param("search") String search,
            Pageable pageable
    );

    @Query("SELECT DISTINCT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.categories oc " +
            "LEFT JOIN FETCH oc.category c " +
            "WHERE o.clientId = :clientId AND o.status IN :statuses " +
            "ORDER BY o.createdDate DESC")
    List<OrderEntity> findAllClientActiveOrdersWithCategories(
            @Param("clientId") String clientId,
            @Param("statuses") List<OrderStatusEnum> statuses
    );

    @Query(value = "SELECT " +
            "COUNT(o.id) AS totalCount, " +
            "COALESCE(SUM(CASE WHEN o.status IN ('PENDING', 'ACCEPTED', 'EN_ROUTE', 'IN_PROGRESS') THEN 1 ELSE 0 END), 0) AS activeCount, " +
            "COALESCE(SUM(CASE WHEN o.status IN ('COMPLETED', 'CONFIRMED', 'PAID', 'REVIEWED') THEN 1 ELSE 0 END), 0) AS completedCount " +
            "FROM orders o WHERE o.client_id = :clientId", nativeQuery = true)
    OrderStatisticsMapper getClientOrderStatistics(@Param("clientId") String clientId);

    @Query("SELECT o.id FROM OrderEntity o " +
            "WHERE o.status = :status " +
            "AND o.id NOT IN :excludedIds " +
            "ORDER BY o.createdDate DESC")
    List<String> findLatestOrderIdsExcluding(
            @Param("status") OrderStatusEnum status,
            @Param("excludedIds") Collection<String> excludedIds,
            Pageable pageable
    );

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT o FROM OrderEntity o WHERE o.id = :id")
    Optional<OrderEntity> findByIdForUpdate(@Param("id") String id);

    @org.springframework.data.jpa.repository.Query("SELECT o FROM OrderEntity o " +
            "LEFT JOIN FETCH o.client " +
            "LEFT JOIN FETCH o.master " +
            "LEFT JOIN FETCH o.categories oc " +
            "LEFT JOIN FETCH oc.category " +
            "WHERE o.id = :id")
    java.util.Optional<OrderEntity> findByIdWithDetails(@org.springframework.data.repository.query.Param("id") String id);

    // Mijozning o'z buyurtmalari ro'yxati
    @org.springframework.data.jpa.repository.Query("SELECT o FROM OrderEntity o WHERE o.clientId = :clientId ORDER BY o.createdDate DESC")
    org.springframework.data.domain.Page<OrderEntity> findByClientId(@org.springframework.data.repository.query.Param("clientId") String clientId, org.springframework.data.domain.Pageable pageable);

    // Ustaning o'zi qabul qilgan buyurtmalari ro'yxati
    @org.springframework.data.jpa.repository.Query("SELECT o FROM OrderEntity o WHERE o.masterId = :masterId ORDER BY o.createdDate DESC")
    org.springframework.data.domain.Page<OrderEntity> findByMasterId(@org.springframework.data.repository.query.Param("masterId") String masterId, org.springframework.data.domain.Pageable pageable);
}