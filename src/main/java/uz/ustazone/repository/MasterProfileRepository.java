package uz.ustazone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uz.ustazone.entity.MasterProfileEntity;
import uz.ustazone.enums.ProfileRoleEnum;

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

}
