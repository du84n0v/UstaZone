package uz.ustazone.service.general;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uz.ustazone.dto.profile.general.ProfileBalanceDTO;
import uz.ustazone.entity.MasterProfileEntity;
import uz.ustazone.entity.ProfileEntity;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.MasterProfileRepository;
import uz.ustazone.repository.ProfileRepository;
import uz.ustazone.util.SpringSecurityUtil;

import java.math.BigDecimal;

@Service
public class ProfileBalanceService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private MasterProfileRepository masterProfileRepository;

    public ProfileBalanceDTO getMyBalance() {
        String profileId = SpringSecurityUtil.getCurrentProfileId();
        
        ProfileEntity profile = profileRepository.findById(profileId)
                .orElseThrow(() -> new ItemNotFoundException("Foydalanuvchi topilmadi"));

        ProfileBalanceDTO dto = new ProfileBalanceDTO();
        dto.setBonusBalance(profile.getBonusBalance() != null ? profile.getBonusBalance() : BigDecimal.ZERO);

        // Agar foydalanuvchi Usta bo'lsa, asosiy balansi master_profile dan olinadi
        if (profile.getRole().name().equals("ROLE_MASTER")) {
            MasterProfileEntity masterProfile = masterProfileRepository.findByProfileId(profileId)
                    .orElseThrow(() -> new ItemNotFoundException("Id not found"));
            if (masterProfile != null && masterProfile.getBalance() != null) {
                dto.setBalance(masterProfile.getBalance());
            } else {
                dto.setBalance(BigDecimal.ZERO);
            }
        } else {
            // Mijozlarda faqat bonus balance bo'ladi
            dto.setBalance(BigDecimal.ZERO);
        }

        return dto;
    }
}