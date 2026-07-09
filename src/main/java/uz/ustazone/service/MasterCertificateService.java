package uz.ustazone.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.ustazone.entity.MasterCertificateEntity;
import uz.ustazone.repository.MasterCertificateRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class MasterCertificateService {

    @Autowired
    private MasterCertificateRepository masterCertificateRepository;
    @Autowired
    private AttachService attachService;

    public void deleteProfileCertificates(String profileId) {
        for(String id :masterCertificateRepository.getProfileCertificateIds(profileId)){
            attachService.delete(id);
        }
        masterCertificateRepository.deleteByMasterProfileId(profileId);
    }

    public void merge(String profileId, List<MultipartFile> certificates) {
        deleteProfileCertificates(profileId);
        List<MasterCertificateEntity> ls = new ArrayList<>();
        for (MultipartFile certificate : certificates) {
            MasterCertificateEntity entity = new MasterCertificateEntity();
            entity.setMasterProfileId(profileId);
            entity.setAttachId(attachService.upload(certificate));

            ls.add(entity);
        }

        masterCertificateRepository.saveAll(ls);
    }
}
