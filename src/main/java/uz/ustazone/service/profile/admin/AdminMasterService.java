package uz.ustazone.service.profile.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.profile.admin.AdminMasterResponseDTO;
import uz.ustazone.entity.MasterProfileEntity;
import uz.ustazone.entity.ProfileEntity;
import uz.ustazone.enums.ProfileRoleEnum;
import uz.ustazone.enums.GeneralStatusEnum; // ACTIVE, PENDING, BLOCKED va h.k.
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.MasterProfileRepository;
import uz.ustazone.repository.ProfileRepository;

@Service
@RequiredArgsConstructor
public class AdminMasterService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private MasterProfileRepository masterProfileRepository;

    @Transactional(readOnly = true)
    public Page<AdminMasterResponseDTO> getMastersList(String search, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);

        // Agar qidiruv so'rovi bo'sh bo'lsa, SQL xato bermasligi uchun uni null qilamiz
        String searchParam = (search == null || search.trim().isEmpty()) ? null : "%" + search.trim().toLowerCase() + "%";

        Page<MasterProfileEntity> masterPage = masterProfileRepository.findMastersForAdmin(
                ProfileRoleEnum.ROLE_MASTER,
                searchParam,
                pageable
        );

        // Entity ob'ektlarini DTO ko'rinishiga o'giramiz
        return masterPage.map(mp -> {
            ProfileEntity profile = mp.getProfile();
            return new AdminMasterResponseDTO(
                    profile.getId(),
                    profile.getFullName(),
                    profile.getPhone(),
                    profile.getCity(),
                    mp.getRating(),
                    mp.getJobsCompleted(),
                    mp.getBalance(),
                    profile.getStatus()
                );
            }
        );
    }

    @Transactional
    public void rejectMaster(String masterId) {
        int effRow = profileRepository.blockMaster(masterId, GeneralStatusEnum.BLOCKED);

        if(effRow == 0){
            throw new ItemNotFoundException("Master not found");
        }
    }
}