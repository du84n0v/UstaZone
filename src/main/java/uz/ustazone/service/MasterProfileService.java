package uz.ustazone.service;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import uz.ustazone.dto.auth.MasterRegisterDTO;
import uz.ustazone.dto.auth.VerificationDTO;
import uz.ustazone.dto.profile.EmailVerifyRequestDTO;
import uz.ustazone.dto.profile.PasswordUpdateDTO;
import uz.ustazone.dto.profile.master.LocationDTO;
import uz.ustazone.dto.profile.master.MasterCabinetResponseDTO;
import uz.ustazone.dto.profile.master.MasterProfileUpdateDTO;
import uz.ustazone.dto.profile.master.ProfessionalDTO;
import uz.ustazone.entity.MasterProfileEntity;
import uz.ustazone.entity.ProfileEntity;
import uz.ustazone.enums.OrderStatusEnum;
import uz.ustazone.exception.AppBadRequestException;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.MasterProfileRepository;
import uz.ustazone.repository.OrderRepository;
import uz.ustazone.repository.ProfileRepository;

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
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;
    @Autowired
    private MailSenderService senderService;
    @Autowired
    private VerificationService verificationService;

    public void save(
            String profileId,
            MasterRegisterDTO dto,
            MultipartFile passportImg,
            MultipartFile selfieImg,
            List<MultipartFile> certificates) {

        Optional<MasterProfileEntity> optional = masterProfileRepository.findById(profileId);
        if (optional.isPresent()) {
            if (optional.get().getIdDocumentAttachId() != null)
                attachService.delete(optional.get().getIdDocumentAttachId());
            if (optional.get().getSelfieAttachId() != null) attachService.delete(optional.get().getSelfieAttachId());
        }

        MasterProfileEntity master = new MasterProfileEntity();
        master.setId(profileId);
        master.setBio(dto.getBio());
        master.setExperienceYears(dto.getExperienceYear());
        master.setServiceRadiusKm(dto.getServiceRadiusKm());
        master.setAcceptsEmergency(dto.getAcceptEmergencyCalls());
        if (dto.getBankAccountNumber() != null) master.setCardNumber(dto.getBankCardNumber());
        if (dto.getBankAccountNumber() != null) master.setBankAccount(dto.getBankAccountNumber());

        masterCategoryService.merge(profileId, dto.getCategoryIds());

        masterWorkDayService.merge(profileId, dto.getWorkDays(), dto.getWorkStart(), dto.getWorkEnd());

        if (!passportImg.isEmpty()) {
            master.setIdDocumentAttachId(attachService.upload(passportImg));
        }
        if (!selfieImg.isEmpty()) {
            master.setSelfieAttachId(attachService.upload(selfieImg));
        }

        masterCertificateService.merge(profileId, certificates);

        masterProfileRepository.save(master);
    }

    public MasterCabinetResponseDTO getMasterCabinetInfo(String masterId) {
        // 1. Ustaning o'zini bazadan topamiz
        MasterProfileEntity masterProfile = masterProfileRepository.findById(masterId)
                .orElseThrow(() -> new ItemNotFoundException("Usta topilmadi"));

        ProfileEntity profile = profileRepository.findById(masterId).get();

        // 2. Jarayondagi statuslar ro'yxati
        List<OrderStatusEnum> inProgressStatuses = List.of(
                OrderStatusEnum.ACCEPTED,
                OrderStatusEnum.EN_ROUTE,
                OrderStatusEnum.IN_PROGRESS
        );

        // 3. Buyurtmalar sonini hisoblaymiz (Boya yozgan IN so'rovimiz orqali)
        Long inProgressCount = orderRepository.countByMasterIdAndStatusIn(masterId, inProgressStatuses);
        Long completedCount = orderRepository.countByMasterIdAndStatus(masterId, OrderStatusEnum.COMPLETED);

        // 4. Sub-DTO'larni to'ldiramiz
        LocationDTO location = new LocationDTO(
                profile.getRegion(),
                profile.getCity(),
                profile.getLatitude(),
                profile.getLongitude()
        );

        ProfessionalDTO profInfo = new ProfessionalDTO(
                masterProfile.getBio(), // yoki profile.getAboutMe()
                masterProfile.getMasterCategories().stream()
                        .map(pc -> pc.getCategory().getNameUz())
                        .toList()// agar tasmali skillar jadvali bo'lsa o'sha yerdan list olinadi
        );

        String avaUrl = profile.getAvatarAttachId() != null ? attachService.openURL(profile.getAvatarAttachId()) : null;

        // 5. Hammasini bitta DTO'ga joylab qaytaramiz
        return MasterCabinetResponseDTO.builder()
                .id(masterProfile.getId())
                .fullName(profile.getFullName())
                .phone(profile.getPhone())
                .email(profile.getEmail())
                .avatarUrl(avaUrl)
                .status(profile.getStatus())
                .isVerified(masterProfile.getIsVerified())
                .balance(masterProfile.getBalance())
                .bonusBalance(profile.getBonusBalance())
                .location(location)
                .profInfo(profInfo)
                .completedOrdersCount(completedCount)
                .inProgressOrdersCount(inProgressCount)
                .build();
    }

    @Transactional
    public String updateMasterDetail(String masterId, MasterProfileUpdateDTO dto) {
        MasterProfileEntity masterProfile = masterProfileRepository.findById(masterId)
                .orElseThrow(() -> new ItemNotFoundException("Usta topiladi"));

        ProfileEntity profile = profileRepository.findById(masterId)
                .orElseThrow(() -> new ItemNotFoundException("Usta topilmadi"));

        if (dto.getFullName() != null) profile.setFullName(dto.getFullName());
        if (dto.getAvatarAttachId() != null) profile.setAvatarAttachId(dto.getAvatarAttachId());
        if (dto.getRegion() != null) profile.setRegion(dto.getRegion());
        if (dto.getCity() != null) profile.setCity(dto.getCity());
        if (dto.getLatitude() != null) profile.setLatitude(dto.getLatitude());
        if (dto.getLongitude() != null) profile.setLongitude(dto.getLongitude());

        if (dto.getBio() != null) masterProfile.setBio(dto.getBio());
        if (dto.getSearchRadius() != null) masterProfile.setServiceRadiusKm(dto.getSearchRadius());
        masterCategoryService.merge(masterId, dto.getSkills());

        profileRepository.save(profile);
        masterProfileRepository.save(masterProfile);

        return "Successfully updated";
    }

    public String updatePassword(String masterId, PasswordUpdateDTO dto) {
        ProfileEntity profile = profileRepository.getById(masterId)
                .orElseThrow(() -> new ItemNotFoundException("User not found"));

        if (!passwordEncoder.matches(dto.getOldPassword(), profile.getPassword())) {
            throw new AppBadRequestException("Old password is not correct!");
        }
        profile.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        profileRepository.save(profile);
        return "Successfully changed";
    }

    public String updateEmail(String masterId, String newEmail) {
        ProfileEntity profile = profileRepository.findById(masterId)
                .orElseThrow(() -> new ItemNotFoundException("Profile not found"));

        senderService.verificationCode(newEmail);

        return "Kod email ga yuborildi";
    }

    @Transactional
    public String verifyAndChangeEmail(String masterId, EmailVerifyRequestDTO dto) {
        verificationService.checkVerificationCode(dto.getNewEmail(), dto.getCode());

        ProfileEntity master = profileRepository.findById(masterId)
                .orElseThrow(() -> new ItemNotFoundException("Usta topilmadi"));

        master.setEmail(dto.getNewEmail());
        profileRepository.save(master);

        return "Email muvafaqiyatli ozgartirildi";
    }
}
