package uz.ustazone.service.profile.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.profile.client.FavoriteMasterResponseDTO;
import uz.ustazone.entity.FavoriteMasterEntity;
import uz.ustazone.entity.ProfileEntity;
import uz.ustazone.enums.ProfileRoleEnum;
import uz.ustazone.exception.AppBadRequestException;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.mapper.FavoriteMasterViewMapper;
import uz.ustazone.repository.FavoriteMasterRepository;
import uz.ustazone.repository.ProfileRepository;
import uz.ustazone.service.general.AttachService;

import java.util.Optional;

@Service
public class ClientFavoriteMasterService {

    @Autowired
    private FavoriteMasterRepository favoriteMasterRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private AttachService attachService;

    // 1. Sevimlilarga qo'shish / o'chirish (Toggle)
    @Transactional
    public String toggleFavorite(String clientId, String masterId) {
        if (clientId.equals(masterId)) {
            throw new AppBadRequestException("O'zingizni sevimlilarga qo'sha olmaysiz!");
        }

        // Faqat haqiqiy ustani qo'shish mumkin
        ProfileEntity master = profileRepository.findById(masterId)
                .filter(p -> Boolean.TRUE.equals(p.getVisible()))
                .orElseThrow(() -> new ItemNotFoundException("Usta topilmadi"));

        if (!ProfileRoleEnum.ROLE_MASTER.equals(master.getRole())) {
            throw new AppBadRequestException("Faqat ustalarni sevimlilarga qo'shish mumkin!");
        }

        Optional<FavoriteMasterEntity> existing = favoriteMasterRepository.findByClientIdAndMasterId(clientId, masterId);

        if (existing.isPresent()) {
            favoriteMasterRepository.delete(existing.get());
            return "Sevimlilardan olib tashlandi"; // Sevimlilardan olib tashlandi (Yurakcha o'chdi)
        } else {
            FavoriteMasterEntity favorite = FavoriteMasterEntity.builder()
                    .clientId(clientId)
                    .masterId(masterId)
                    .build();
            favoriteMasterRepository.save(favorite);
            return "Sevimlilarga qoshildi"; // Sevimlilarga qo'shildi (Yurakcha yondi)
        }
    }

    // 2. Mijozning sevimli ustalari ro'yxati
    @Transactional(readOnly = true)
    public Page<FavoriteMasterResponseDTO> getFavorites(String clientId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<FavoriteMasterViewMapper> mappers = favoriteMasterRepository.findFavoritesByClientId(clientId, pageable);

        return mappers.map(m -> FavoriteMasterResponseDTO.builder()
                .masterId(m.getMasterId())
                .fullName(m.getFullName())
                .phone(m.getPhone())
                .address(m.getAddress())
                .region(m.getRegion())
                .avatarUrl(m.getAvatarAttachId() != null ? attachService.openURL(m.getAvatarAttachId()) : null)
                .savedDate(m.getSavedDate())
                .build()
        );
    }

    // 3. Usta profiliga kirganda yurakcha qizil turishi kerakmi-yo'qmi tekshirish
    @Transactional(readOnly = true)
    public boolean isFavorite(String clientId, String masterId) {
        return favoriteMasterRepository.existsByClientIdAndMasterId(clientId, masterId);
    }
}