package uz.ustazone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uz.ustazone.entity.MasterCategoryEntity;
import uz.ustazone.entity.MasterProfileEntity;
import uz.ustazone.enums.ProfileRoleEnum;

import java.util.List;
import java.util.Optional;

public interface MasterProfileRepository extends CrudRepository<MasterProfileEntity, String> {

    @Query("SELECT mp FROM MasterProfileEntity mp " +
            "INNER JOIN FETCH mp.profile p " + // FETCH qo'shildi, N+1 muammosi hal bo'ldi
            "WHERE p.role = :role " +          // WHERE kalit so'zi tushib qolgan edi
            "AND (:search IS NULL OR " +
            "     LOWER(p.fullName) LIKE :search OR " +
            "     p.phone LIKE :search) " + // p. dan keyin maydon nomi yo'q edi
            "ORDER BY p.createdDate DESC")
    Page<MasterProfileEntity> findMastersForAdmin(
            @Param("role") ProfileRoleEnum role,
            @Param("search") String search,
            Pageable pageable
    );

    Optional<MasterProfileEntity> findByProfileId(String profileId);

    // Ustani kategoriyalari bilan birga N+1 siz bitta so'rovda olib chiqish
    @Query("SELECT mp FROM MasterProfileEntity mp " +
            "LEFT JOIN FETCH mp.masterCategories mc " +
            "LEFT JOIN FETCH mc.category " +
            "WHERE mp.id = :id")
    java.util.Optional<uz.ustazone.entity.MasterProfileEntity> findByIdWithCategories(@Param("id") String id);

    // Ustaga tegishli xizmatlarni (narxlari bilan) N+1 siz olish
    @Query("SELECT mc FROM MasterCategoryEntity mc " +
            "JOIN FETCH mc.category " +
            "WHERE mc.masterProfile.id = :masterId")
    List<MasterCategoryEntity> findAllByMasterIdWithCategory(@Param("masterId") String masterId);

    @Query("SELECT DISTINCT mp FROM MasterProfileEntity mp " +
            "JOIN mp.profile p " +
            "LEFT JOIN mp.masterCategories mc " +
            "WHERE p.status = 'ACTIVE' AND p.visible = true AND (p.isBlocked IS NULL OR p.isBlocked = false) " +
            "AND (:city IS NULL OR LOWER(p.city) LIKE LOWER(CONCAT('%', :city, '%'))) " +
            "AND (:categoryId IS NULL OR mc.categoryId = :categoryId)")
    Page<MasterProfileEntity> findPublicMasters(
            @Param("city") String city,
            @Param("categoryId") String categoryId,
            Pageable pageable);
}
