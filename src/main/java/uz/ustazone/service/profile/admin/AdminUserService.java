package uz.ustazone.service.profile.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.profile.admin.AdminUserResponseDTO;
import uz.ustazone.entity.ProfileEntity;
import uz.ustazone.enums.GeneralStatusEnum;
import uz.ustazone.enums.ProfileRoleEnum;
import uz.ustazone.exception.BaseException;
import uz.ustazone.repository.ProfileRepository;

@Service
@RequiredArgsConstructor
public class AdminUserService {

    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public Page<AdminUserResponseDTO> getUsers(String search, Pageable pageable) {
        String searchParam = (search != null && !search.isBlank()) 
                ? "%" + search.trim().toLowerCase() + "%" 
                : null;

        Page<ProfileEntity> profiles = profileRepository.findUsersForAdmin(searchParam, pageable);

        return profiles.map(p -> new AdminUserResponseDTO(
                p.getId(),
                p.getFullName(),
                p.getPhone() != null ? p.getPhone() : "-",
                p.getCity() != null ? p.getCity() : "Toshkent",
                p.getRole(),
                p.getStatus(),
                p.getIsBlocked() != null ? p.getIsBlocked() : false,
                p.getCreatedDate() != null ? p.getCreatedDate().toLocalDate() : null
        ));
    }

    @Transactional
    public void toggleBlock(String id) {
        ProfileEntity profile = profileRepository.findByIdAndVisibleTrue(id)
                .orElseThrow(() -> new BaseException("Foydalanuvchi topilmadi"));

        // Adminni bloklash taqiqlanadi
        if (profile.getRole() == ProfileRoleEnum.ROLE_ADMIN) {
            throw new BaseException("Admin akkauntini bloklash mumkin emas!");
        }

        boolean currentBlockedStatus = profile.getIsBlocked() != null && profile.getIsBlocked();
        profile.setIsBlocked(!currentBlockedStatus);
        
        // Statusni ham moslashtirib qo'yamiz
        profile.setStatus(profile.getIsBlocked() ? GeneralStatusEnum.BLOCKED : GeneralStatusEnum.ACTIVE);
        
        profileRepository.save(profile);
    }

    @Transactional
    public void deleteUser(String id) {
        ProfileEntity profile = profileRepository.findByIdAndVisibleTrue(id)
                .orElseThrow(() -> new BaseException("Foydalanuvchi topilmadi"));

        // Adminni o'chirish taqiqlanadi
        if (profile.getRole() == ProfileRoleEnum.ROLE_ADMIN) {
            throw new BaseException("Admin akkauntini o'chirish mumkin emas!");
        }

        // Soft delete (tizimdan yashirish)
        profile.setVisible(false);
        profileRepository.save(profile);
    }
}