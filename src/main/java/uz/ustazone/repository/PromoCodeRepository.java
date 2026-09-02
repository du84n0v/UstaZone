package uz.ustazone.repository;

import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.PromoCodeEntity;

import java.util.List;

public interface PromoCodeRepository extends CrudRepository<PromoCodeEntity, String> {

    // Bir xil kod qayta ochilmasligi uchun tekshiruv
    boolean existsByCodeAndVisibleTrue(String code);

    // O'chirilmagan barcha promokodlar (eng yangisi tepada)
    List<PromoCodeEntity> findAllByVisibleTrueOrderByCreatedDateDesc();
}