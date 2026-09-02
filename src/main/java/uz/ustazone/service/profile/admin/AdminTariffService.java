package uz.ustazone.service.profile.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.profile.admin.AdminTariffRequestDTO;
import uz.ustazone.dto.profile.admin.AdminTariffResponseDTO;
import uz.ustazone.entity.TariffEntity;
import uz.ustazone.enums.TariffAudienceEnum;
import uz.ustazone.enums.TariffTierEnum;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.TariffRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AdminTariffService {

    @Autowired
    private TariffRepository tariffRepository;

    public AdminTariffResponseDTO create(AdminTariffRequestDTO dto) {
        TariffEntity entity = TariffEntity.builder()
                .audience(dto.getAudience())
                .tierCode(dto.getTierCode().toLowerCase().trim())
                .tierName(TariffTierEnum.valueOf(dto.getTierName()))
                .icon(dto.getIcon())
                .badge(dto.getBadge())
                .price1Month(dto.getPrice1Month())
                .price3Month(dto.getPrice3Month() != null ? dto.getPrice3Month() : BigDecimal.ZERO)
                .price6Month(dto.getPrice6Month() != null ? dto.getPrice6Month() : BigDecimal.ZERO)
                .price12Month(dto.getPrice12Month())
                .features(dto.getFeatures())
                .sortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0)
                .isPopular(dto.getIsPopular() != null ? dto.getIsPopular() : false)
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();

        tariffRepository.save(entity);
        return toDto(entity);
    }

    public AdminTariffResponseDTO update(String id, AdminTariffRequestDTO dto) {
        TariffEntity entity = getEntity(id);

        entity.setAudience(dto.getAudience());
        entity.setTierCode(dto.getTierCode().toLowerCase().trim());
        entity.setTierName(TariffTierEnum.valueOf(dto.getTierName()));
        entity.setIcon(dto.getIcon());
        entity.setBadge(dto.getBadge());
        entity.setPrice1Month(dto.getPrice1Month());
        entity.setPrice3Month(dto.getPrice3Month());
        entity.setPrice6Month(dto.getPrice6Month());
        entity.setPrice12Month(dto.getPrice12Month());
        entity.setFeatures(dto.getFeatures());
        entity.setSortOrder(dto.getSortOrder());
        entity.setIsPopular(dto.getIsPopular());
        entity.setIsActive(dto.getIsActive());

        tariffRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<AdminTariffResponseDTO> getAll(TariffAudienceEnum audience) {
        List<TariffEntity> list = (audience != null)
                ? tariffRepository.findAllByAudienceAndVisibleTrueOrderBySortOrderAsc(audience)
                : tariffRepository.findAllByVisibleTrueOrderBySortOrderAsc();

        return list.stream().map(this::toDto).toList();
    }

    @Transactional(readOnly = true)
    public AdminTariffResponseDTO getById(String id) {
        return toDto(getEntity(id));
    }

    public void delete(String id) {
        TariffEntity entity = getEntity(id);
        entity.setVisible(false); // Soft delete
        tariffRepository.save(entity);
    }

    private TariffEntity getEntity(String id) {
        return tariffRepository.findById(id)
                .filter(TariffEntity::getVisible)
                .orElseThrow(() -> new ItemNotFoundException("Tarif topilmadi: " + id));
    }

    private AdminTariffResponseDTO toDto(TariffEntity entity) {
        return AdminTariffResponseDTO.builder()
                .id(entity.getId())
                .audience(entity.getAudience())
                .tierCode(entity.getTierCode())
                .tierName(entity.getTierName())
                .icon(entity.getIcon())
                .badge(entity.getBadge())
                .price1Month(entity.getPrice1Month())
                .price3Month(entity.getPrice3Month())
                .price6Month(entity.getPrice6Month())
                .price12Month(entity.getPrice12Month())
                .features(entity.getFeatures())
                .sortOrder(entity.getSortOrder())
                .isPopular(entity.getIsPopular())
                .isActive(entity.getIsActive())
                .build();
    }
}