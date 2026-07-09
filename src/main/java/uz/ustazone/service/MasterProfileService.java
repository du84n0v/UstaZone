package uz.ustazone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.ustazone.dto.auth.MasterRegisterDTO;
import uz.ustazone.entity.MasterProfileEntity;
import uz.ustazone.repository.MasterProfileRepository;

import java.util.List;
import java.util.Optional;

@Service
public class MasterProfileService {

    @Autowired
    private MasterProfileRepository masterProfileRepository;
    @Autowired
    private AttachService attachService;
    @Autowired
    private MasterCategoryService masterCategoryService;
    @Autowired
    private MasterCertificateService masterCertificateService;
    @Autowired
    private MasterWorkDayService masterWorkDayService;

    public void save(
            String profileId,
            MasterRegisterDTO dto,
            MultipartFile passportImg,
            MultipartFile selfieImg,
            List<MultipartFile> certificates) {

        Optional<MasterProfileEntity> optional = masterProfileRepository.findById(profileId);
        if(optional.isPresent()){
            if(optional.get().getIdDocumentAttachId() != null) attachService.delete(optional.get().getIdDocumentAttachId());
            if(optional.get().getSelfieAttachId() != null) attachService.delete(optional.get().getSelfieAttachId());
        }

        MasterProfileEntity master = new MasterProfileEntity();
        master.setId(profileId);
        master.setBio(dto.getBio());
        master.setExperienceYears(dto.getExperienceYear());
        master.setServiceRadiusKm(dto.getServiceRadiusKm());
        master.setAcceptsEmergency(dto.getAcceptEmergencyCalls());
        if(dto.getBankAccountNumber() != null) master.setCardNumber(dto.getBankCardNumber());
        if(dto.getBankAccountNumber() != null) master.setBankAccount(dto.getBankAccountNumber());

        masterCategoryService.merge(profileId, dto.getCategoryIds());

        masterWorkDayService.merge(profileId, dto.getWorkDays(), dto.getWorkStart(), dto.getWorkEnd());

        if(!passportImg.isEmpty()){
            master.setIdDocumentAttachId(attachService.upload(passportImg));
        }
        if(!selfieImg.isEmpty()){
            master.setSelfieAttachId(attachService.upload(selfieImg));
        }

        masterCertificateService.merge(profileId, certificates);

        masterProfileRepository.save(master);
    }
}
