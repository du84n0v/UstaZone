package uz.ustazone.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import uz.ustazone.entity.FavoriteMasterEntity;
import uz.ustazone.mapper.FavoriteMasterViewMapper;

import java.util.Optional;

public interface FavoriteMasterRepository extends CrudRepository<FavoriteMasterEntity, String> {

    boolean existsByClientIdAndMasterId(String clientId, String masterId);

    Optional<FavoriteMasterEntity> findByClientIdAndMasterId(String clientId, String masterId);

    @Query("SELECT " +
            " m.id AS masterId, " +
            " m.fullName AS fullName, " +
            " m.phone AS phone, " +
            " m.city AS city, " +
            " m.region AS region, " +
            " m.avatarAttachId AS avatarAttachId, " +
            " f.createdDate AS savedDate " +
            " FROM FavoriteMasterEntity f " +
            " JOIN f.master m " +
            " WHERE f.clientId = :clientId " +
            " AND m.visible = true " +
            " ORDER BY f.createdDate DESC")
    Page<FavoriteMasterViewMapper> findFavoritesByClientId(@Param("clientId") String clientId, Pageable pageable);
}