package uz.ustazone.repository;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.MasterCategoryEntity;
import uz.ustazone.enums.PriceTypeEnum;

import java.math.BigDecimal;
import java.util.List;

public interface MasterCategoryRepository extends CrudRepository<MasterCategoryEntity, String> {
    void deleteByMasterProfileId(String masterProfileId);

    List<MasterCategoryEntity> findAllByMasterProfileIdAndCategoryParentIsNotNull(String masterId);

    @Transactional
    @Modifying
    @Query("UPDATE MasterCategoryEntity mc " +
            " SET mc.priceType = ?2, mc.minPrice = ?3, mc.maxPrice = ?4 " +
            " WHERE mc.id = ?1")
    int updateCategoryPrice(String id, PriceTypeEnum priceType, BigDecimal minPrice, BigDecimal maxPrice);
}
