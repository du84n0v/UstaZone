package uz.ustazone.service.profile.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import uz.ustazone.dto.profile.admin.AdminVerificationViewDTO;
import uz.ustazone.enums.GeneralStatusEnum;
import uz.ustazone.enums.VerificationStatusEnum;
import uz.ustazone.mapper.admin.AdminVerificationMastersMapper;
import uz.ustazone.repository.MasterVerificationRepository;
import uz.ustazone.repository.ProfileRepository;

@Service
public class AdminVerificationService {

    @Autowired
    private MasterVerificationRepository masterVerificationRepository;

    public Page<AdminVerificationViewDTO> getMastersByStatus(VerificationStatusEnum status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<AdminVerificationMastersMapper> pages = masterVerificationRepository.getActiveMaster(
                pageable,
                GeneralStatusEnum.ACTIVE,
                status);

        return pages.map(cur -> AdminVerificationViewDTO.builder()
                .id(cur.getId())
                .masterId(cur.getMasterId())
                .fullName(cur.getFullName())
                .phone(cur.getPhone())
                .status(cur.getStatus())
                .createdDate(cur.getCreatedDate())
                .build());
    }
}
