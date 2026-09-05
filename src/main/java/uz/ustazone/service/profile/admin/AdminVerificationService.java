package uz.ustazone.service.profile.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.profile.admin.AdminVerificationDetailResponseDTO;
import uz.ustazone.dto.profile.admin.AdminVerificationRejectRequestDTO;
import uz.ustazone.dto.profile.admin.AdminVerificationViewDTO;
import uz.ustazone.entity.MasterProfileEntity;
import uz.ustazone.entity.MasterVerificationEntity;
import uz.ustazone.entity.ProfileEntity;
import uz.ustazone.enums.GeneralStatusEnum;
import uz.ustazone.enums.VerificationStatusEnum;
import uz.ustazone.exception.AppBadRequestException;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.mapper.admin.AdminVerificationViewMapper;
import uz.ustazone.repository.MasterProfileRepository;
import uz.ustazone.repository.MasterVerificationRepository;
import uz.ustazone.service.general.AttachService;
import uz.ustazone.service.profile.master.MasterCertificateService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Service
public class AdminVerificationService {

    @Autowired
    private MasterVerificationRepository masterVerificationRepository;
    @Autowired
    private MasterProfileRepository masterProfileRepository;
    @Autowired
    private AttachService attachService;
    @Autowired
    private MasterCertificateService masterCertificateService;

    @Transactional(readOnly = true)
    public Page<AdminVerificationViewDTO> getApplicationsByStatus(VerificationStatusEnum status, int page, int size) {
        VerificationStatusEnum targetStatus = (status != null) ? status : VerificationStatusEnum.PENDING;
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdDate"));

        Page<AdminVerificationViewMapper> pages = masterVerificationRepository.getApplicationByStatus(
                pageable,
                GeneralStatusEnum.ACTIVE,
                targetStatus);

        return pages.map(this::viewMapperToDto);
    }

    @Transactional(readOnly = true)
    public AdminVerificationDetailResponseDTO getApplicationById(String id) {
        // Ariza ID bo'yicha qidiriladi
        AdminVerificationViewMapper mapper = masterVerificationRepository.getVerMapperById(id);
        if (mapper == null) {
            throw new ItemNotFoundException("Verifikatsiya arizasi topilmadi: " + id);
        }

        MasterProfileEntity profile = masterProfileRepository.findById(mapper.getMasterId())
                .orElseThrow(() -> new ItemNotFoundException("Master profili topilmadi: " + mapper.getMasterId()));

        String passportUrl = (profile.getIdDocumentAttachId() != null)
                ? attachService.openURL(profile.getIdDocumentAttachId())
                : null;

        String selfieUrl = (profile.getSelfieAttachId() != null)
                ? attachService.openURL(profile.getSelfieAttachId())
                : null;

        // E'tibor bering: id emas, mapper.getMasterId() berilishi kerak
        List<String> certificateIds = masterCertificateService.getMasterCertificates(mapper.getMasterId());
        List<String> certificateUrls = (certificateIds != null)
                ? certificateIds.stream().map(attachService::openURL).toList()
                : Collections.emptyList();

        MasterVerificationEntity application = masterVerificationRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Application not found"));

        AdminVerificationDetailResponseDTO response = new AdminVerificationDetailResponseDTO();
        response.setViewDto(viewMapperToDto(mapper));
        response.setPassportAttachId(passportUrl);
        response.setSelfieAttachId(selfieUrl);
        response.setCertificates(certificateUrls);
        response.setRejectReason(application.getRejectReason());

        return response;
    }

    public AdminVerificationViewDTO viewMapperToDto(AdminVerificationViewMapper mapper){
        return new AdminVerificationViewDTO(
                mapper.getId(),
                mapper.getMasterId(),
                mapper.getFullName(),
                mapper.getPhone(),
                mapper.getStatus(),
                mapper.getCreatedDate()
                );
    }

    @Transactional
    public String approveApplication(String id) {
        MasterVerificationEntity application = masterVerificationRepository.findById(id)
                .orElseThrow(() -> new ItemNotFoundException("Application not found"));

        if (!VerificationStatusEnum.PENDING.equals(application.getStatus())) {
            throw new AppBadRequestException("Bu ariza allaqachon ko'rib chiqilgan!");
        }

        application.setStatus(VerificationStatusEnum.APPROVED);
        application.setReviewDate(LocalDateTime.now());

        ProfileEntity profile = application.getProfile();
        profile.setIsVerified(true);

        MasterProfileEntity master = masterProfileRepository.findById(profile.getId())
                        .orElseThrow(() -> new ItemNotFoundException("Master not found"));
        master.setIsVerified(true);

        return "Successfully approved";
    }

    @Transactional
    public String rejectApplication(AdminVerificationRejectRequestDTO dto) {
        MasterVerificationEntity application = masterVerificationRepository.findById(dto.getId())
                .orElseThrow(() -> new ItemNotFoundException("Application not found"));

        if (!VerificationStatusEnum.PENDING.equals(application.getStatus())) {
            throw new AppBadRequestException("Bu ariza allaqachon ko'rib chiqilgan!");
        }

        application.setStatus(VerificationStatusEnum.REJECTED);
        application.setRejectReason(dto.getReason());
        application.setReviewDate(LocalDateTime.now());

        ProfileEntity profile = application.getProfile();
        profile.setIsVerified(false);

        MasterProfileEntity master = masterProfileRepository.findById(profile.getId())
                .orElseThrow(() -> new ItemNotFoundException("Master not found"));
        master.setIsVerified(false);

        return "Successfully rejected";
    }
}
