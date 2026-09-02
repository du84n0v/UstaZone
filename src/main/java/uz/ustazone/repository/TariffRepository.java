package uz.ustazone.repository;

import org.springframework.data.repository.CrudRepository;
import uz.ustazone.entity.TariffEntity;
import uz.ustazone.enums.TariffAudienceEnum;

import java.util.List;

public interface TariffRepository extends CrudRepository<TariffEntity, String> {

    // O'chirilmagan barcha tariflar (tartib bo'yicha)
    List<TariffEntity> findAllByVisibleTrueOrderBySortOrderAsc();

    // Auditoriya bo'yicha ajratib olish (Usta yoki Mijoz tariflari alohida kerak bo'lsa)
    List<TariffEntity> findAllByAudienceAndVisibleTrueOrderBySortOrderAsc(TariffAudienceEnum audience);
}