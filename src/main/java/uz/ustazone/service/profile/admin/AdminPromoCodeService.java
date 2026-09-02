package uz.ustazone.service.profile.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.profile.admin.AdminPromoCodeCreateDTO;
import uz.ustazone.dto.profile.admin.AdminPromoCodeResponseDTO;
import uz.ustazone.entity.PromoCodeEntity;
import uz.ustazone.enums.PromoCodeTypeEnum;
import uz.ustazone.exception.AppBadRequestException;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.PromoCodeRepository;

import java.util.List;

@Service
public class AdminPromoCodeService {

    @Autowired
    private PromoCodeRepository promoCodeRepository;

    @Transactional
    public AdminPromoCodeResponseDTO create(AdminPromoCodeCreateDTO dto) {
        String formattedCode = dto.getCode().trim().toUpperCase();

        if (promoCodeRepository.existsByCodeAndVisibleTrue(formattedCode)) {
            throw new AppBadRequestException("Bu promokod allaqachon mavjud: " + formattedCode);
        }

        if (dto.getType() == PromoCodeTypeEnum.PERCENT && dto.getDiscountValue() > 100) {
            throw new AppBadRequestException("Foiz miqdori 100 dan oshishi mumkin emas!");
        }

        PromoCodeEntity entity = PromoCodeEntity.builder()
                .code(formattedCode)
                .type(dto.getType())
                .discountValue(dto.getDiscountValue())
                .maxUsageCount(dto.getMaxUsageCount())
                .usedCount(0)
                .minOrderAmount(dto.getMinOrderAmount() != null ? dto.getMinOrderAmount() : 0L)
                .isActive(true)
                .visible(true)
                .build();

        promoCodeRepository.save(entity);
        return toDto(entity);
    }

    @Transactional(readOnly = true)
    public List<AdminPromoCodeResponseDTO> getAll() {
        return promoCodeRepository.findAllByVisibleTrueOrderByCreatedDateDesc()
                .stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public AdminPromoCodeResponseDTO toggleStatus(String id) {
        PromoCodeEntity entity = getEntity(id);
        entity.setIsActive(!entity.getIsActive());
        promoCodeRepository.save(entity);
        return toDto(entity);
    }

    @Transactional
    public void delete(String id) {
        PromoCodeEntity entity = getEntity(id);
        entity.setVisible(false);
        promoCodeRepository.save(entity);
    }

    private PromoCodeEntity getEntity(String id) {
        return promoCodeRepository.findById(id)
                .filter(PromoCodeEntity::getVisible)
                .orElseThrow(() -> new ItemNotFoundException("Promokod topilmadi: " + id));
    }

    private AdminPromoCodeResponseDTO toDto(PromoCodeEntity entity) {
        return AdminPromoCodeResponseDTO.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .type(entity.getType())
                .discountValue(entity.getDiscountValue())
                .maxUsageCount(entity.getMaxUsageCount())
                .usedCount(entity.getUsedCount())
                .minOrderAmount(entity.getMinOrderAmount())
                .isActive(entity.getIsActive())
                .createdDate(entity.getCreatedDate())
                .build();
    }
}