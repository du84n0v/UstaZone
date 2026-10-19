package uz.ustazone.service.profile.master;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.profile.master.MasterCardResponseDTO;
import uz.ustazone.dto.profile.master.MasterPublicProfileDTO;
import uz.ustazone.entity.MasterProfileEntity;
import uz.ustazone.entity.ProfileEntity;
import uz.ustazone.repository.MasterProfileRepository;
import uz.ustazone.service.general.AttachService;
import uz.ustazone.service.profile.client.ClientService;

import java.util.List;

@Service
public class PublicMasterService {

    @Autowired
    private MasterProfileRepository masterProfileRepository;

    @Autowired
    private AttachService attachService;

    @Autowired
    private ClientService clientService;

    @Transactional(readOnly = true)
    public Page<MasterCardResponseDTO> getPublicMasters(String categoryId, String city, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "rating"));
        Page<MasterProfileEntity> masterPage = masterProfileRepository.findPublicMasters(city, categoryId, pageable);

        return masterPage.map(this::toCardDTO);
    }

    @Transactional(readOnly = true)
    public MasterPublicProfileDTO getMasterProfile(String masterId) {
        return clientService.getMasterPublicProfileForClient(masterId);
    }

    private MasterCardResponseDTO toCardDTO(MasterProfileEntity mp) {
        ProfileEntity profile = mp.getProfile();

        List<String> categories = mp.getMasterCategories() != null
                ? mp.getMasterCategories().stream()
                    .filter(mc -> mc.getCategory() != null)
                    .map(mc -> mc.getCategory().getNameUz())
                    .distinct()
                    .toList()
                : List.of();

        String avatarUrl = (profile != null && profile.getAvatarAttachId() != null)
                ? attachService.openURL(profile.getAvatarAttachId())
                : null;

        return MasterCardResponseDTO.builder()
                .id(mp.getId())
                .fullName(profile != null ? profile.getFullName() : "-")
                .avatarUrl(avatarUrl)
                .city(profile != null ? profile.getCity() : null)
                .region(profile != null ? profile.getRegion() : null)
                .rating(mp.getRating() != null ? mp.getRating() : 5.0)
                .completedOrdersCount(mp.getJobsCompleted() != null ? mp.getJobsCompleted().longValue() : 0L)
                .categories(categories)
                .build();
    }
}