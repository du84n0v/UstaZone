package uz.ustazone.service.profile.master;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import uz.ustazone.dto.auth.MasterRegisterDTO;
import uz.ustazone.dto.profile.general.EmailVerifyRequestDTO;
import uz.ustazone.dto.profile.general.PasswordUpdateDTO;
import uz.ustazone.dto.profile.master.*;
import uz.ustazone.entity.*;
import uz.ustazone.enums.OrderStatusEnum;
import uz.ustazone.exception.AppBadRequestException;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.repository.MasterProfileRepository;
import uz.ustazone.repository.MasterWorkDayRepository;
import uz.ustazone.repository.OrderRepository;
import uz.ustazone.repository.ProfileRepository;
import uz.ustazone.service.general.AttachService;
import uz.ustazone.service.general.MailSenderService;
import uz.ustazone.service.general.VerificationService;

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
    @Autowired
    private MasterWorkDayRepository masterWorkDayRepository;

    public void save(
            String profileId,
            MasterRegisterDTO dto) {

        MasterProfileEntity master = new MasterProfileEntity();
        master.setId(profileId);
        master.setBio(dto.getBio());
        master.setExperienceYears(dto.getExperienceYear());
        master.setServiceRadiusKm(dto.getServiceRadiusKm());
        master.setAcceptsEmergency(dto.getAcceptEmergencyCalls());
        if (dto.getBankAccountNumber() != null) master.setCardNumber(dto.getBankCardNumber());
        if (dto.getBankAccountNumber() != null) master.setBankAccount(dto.getBankAccountNumber());

        masterProfileRepository.save(master);

        masterCategoryService.merge(profileId, dto.getCategoryIds());

        masterWorkDayService.merge(profileId, dto.getWorkDays(), dto.getWorkStart(), dto.getWorkEnd());

        masterCertificateService.merge(profileId, dto.getCertificateIds());
    }

    public MasterCabinetResponseDTO getMasterCabinetInfo(String masterId) {
        // 1. Ustaning o'zini bazadan topamiz
        MasterProfileEntity masterProfile = masterProfileRepository.findById(masterId)
                .orElseThrow(() -> new ItemNotFoundException("Usta topilmadi"));

        Optional<ProfileEntity> optional = profileRepository.findById(masterId);
        if (optional.isEmpty()) {
            throw new ItemNotFoundException("Profile not found");
        }
        ProfileEntity profile = optional.get();

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
                        .toList()
        );

        //avatar url
        String avaUrl = profile.getAvatarAttachId() != null ? attachService.openURL(profile.getAvatarAttachId()) : null;

        //category prices
        List<MasterCategoryEntity> masterServices = masterCategoryService.getMasterCategories(masterId);

        List<MasterServicePriceDTO> servicePriceDTOs = masterServices.stream().map(ms ->
                MasterServicePriceDTO.builder()
                        .id(ms.getId()) // MasterCategoryEntity ID-si
                        .subCategoryId(ms.getCategoryId())
                        .subCategoryName(ms.getCategory().getNameUz()) // yoki tilga qarab nameUz, nameRu
                        .priceType(ms.getPriceType())
                        .minPrice(ms.getMinPrice())
                        .maxPrice(ms.getMaxPrice())
                        .build()
        ).toList();

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
                .servicePrices(servicePriceDTOs)
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
        profileRepository.findById(masterId)
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

    public String updatePrices(String masterId, List<MasterServicePriceUpdateDTO> dtoList) {
        profileRepository.getById(masterId)
                .orElseThrow(() -> new ItemNotFoundException("Profile not found"));

        masterCategoryService.updateCategoryPrice(dtoList);

        return "Successfully changed";
    }

    @Transactional
    public String updateSchedule(String masterId, MasterScheduleUpdateDTO dto) {
        masterWorkDayRepository.deleteAllByMasterProfileId(masterId);

        if (dto.getDays() == null || dto.getDays().isEmpty()) {
            return "Successfully updated";
        }

        // 3. Faqat toggle switch yoqilgan (isActive == true) kunlarni Entity-ga o'giramiz
        List<MasterWorkDayEntity> newSchedule = dto.getDays().stream()
                .filter(MasterWorkDayDTO::getIsActive) // Faqat aktiv kunlarni saqlaymiz
                .map(dayDto -> {
                    MasterWorkDayEntity entity = new MasterWorkDayEntity();
                    entity.setMasterProfileId(masterId);
                    entity.setDayOfWeek(dayDto.getDayOfWeek());
                    entity.setWorkStart(dayDto.getWorkStart());
                    entity.setWorkEnd(dayDto.getWorkEnd());
                    return entity;
                })
                .toList();

        // 4. Yangi jadvalni bittada saqlaymiz
        if (!newSchedule.isEmpty()) {
            masterWorkDayRepository.saveAll(newSchedule);
        }

        return "Successfully updated";
    }

}
