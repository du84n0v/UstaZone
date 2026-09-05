package uz.ustazone.service.profile.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.address.ClientAddressResponseDTO;
import uz.ustazone.dto.category.CategoryShortInfoDTO;
import uz.ustazone.dto.order.OrderShortResponseDTO;
import uz.ustazone.dto.profile.client.ClientCabinetResponseDTO;
import uz.ustazone.dto.profile.master.MasterPublicProfileDTO;
import uz.ustazone.dto.profile.master.LocationDTO;
import uz.ustazone.dto.profile.master.MasterServicePriceDTO;
import uz.ustazone.dto.profile.master.MasterWorkDayDTO;
import uz.ustazone.dto.profile.master.ProfessionalDTO;
import uz.ustazone.entity.*;
import uz.ustazone.enums.OrderStatusEnum;
import uz.ustazone.exception.ItemNotFoundException;
import uz.ustazone.mapper.OrderStatisticsMapper;
import uz.ustazone.repository.*;
import uz.ustazone.service.general.AttachService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private MasterProfileRepository masterProfileRepository;
    @Autowired
    private MasterCategoryRepository masterCategoryRepository;
    @Autowired
    private MasterWorkDayRepository masterWorkDayRepository;
    @Autowired
    private AttachService attachService;
    @Autowired
    private ClientAddressRepository clientAddressRepository;

    private static final List<OrderStatusEnum> ACTIVE_STATUSES = List.of(
            OrderStatusEnum.PENDING,
            OrderStatusEnum.ACCEPTED,
            OrderStatusEnum.EN_ROUTE,
            OrderStatusEnum.IN_PROGRESS
    );

    @Transactional(readOnly = true)
    public ClientCabinetResponseDTO getClientCabinetInfo(String clientId) {
        // 1. Profil ma'lumotlari
        ProfileEntity client = profileRepository.findById(clientId)
                .orElseThrow(() -> new ItemNotFoundException("Mijoz topilmadi: " + clientId));

        OrderStatisticsMapper stats = orderRepository.getClientOrderStatistics(clientId);
        Long totalOrders = (stats != null) ? stats.getTotalCount() : 0L;
        Long activeCount = (stats != null) ? stats.getActiveCount() : 0L;
        Long completedCount = (stats != null) ? stats.getCompletedCount() : 0L;

        List<OrderEntity> activeOrderEntities = orderRepository.findAllClientActiveOrdersWithCategories(clientId, ACTIVE_STATUSES);

        List<OrderShortResponseDTO> activeOrderDTOs = activeOrderEntities.stream().map(order ->
                OrderShortResponseDTO.builder()
                        .id(order.getId())
                        .title(order.getTitle())
                        .amount(order.getAmount())
                        .status(order.getStatus().name())
                        .createdDate(order.getCreatedDate())
                        .region(order.getCity())                    // Tushib qolgan maydon
                        .address(order.getAddress())              // Tushib qolgan maydon
                        .isEmergency(order.getIsEmergency())      // Tushib qolgan maydon
                        .categories(order.getCategories().stream()
                                .map(oc -> new CategoryShortInfoDTO(oc.getCategoryId(), oc.getCategory().getNameUz()))
                                .toList())
                        .build()
        ).toList();

        List<ClientAddressEntity> addresses = clientAddressRepository.findAllByClientIdAndVisibleTrueOrderByCreatedDateDesc(clientId);
        List<ClientAddressResponseDTO> addressDTOs = addresses.stream()
                .map(ca -> ClientAddressResponseDTO.builder()
                        .id(ca.getId())
                        .type(ca.getAddressType())
                        .addressLine(ca.getAddressLine())
                        .region(ca.getRegion())
                        .createdDate(ca.getCreatedDate())
                        .build())
                .toList();

        // 5. Bepul PRO muddati hisobi (Masalan, 14 kunlik sinov)
        long daysPassed = ChronoUnit.DAYS.between(client.getCreatedDate(), LocalDateTime.now());
        long proDaysLeft = Math.max(0, 14 - daysPassed);

        return ClientCabinetResponseDTO.builder()
                .id(client.getId())
                .fullName(client.getFullName())
                .bonusBalance(client.getBonusBalance() != null ? client.getBonusBalance() : BigDecimal.ZERO)
                .proTrialDaysLeft(proDaysLeft)
                .isProActive(proDaysLeft > 0)
                .totalOrdersCount(totalOrders)
                .activeOrdersCount(activeCount)
                .completedOrdersCount(completedCount)
                .activeOrders(activeOrderDTOs)
                .addresses(addressDTOs)
                .favoriteMasterIds(Collections.emptyList())
                .build();
    }

    @Transactional(readOnly = true)
    public MasterPublicProfileDTO getMasterPublicProfileForClient(String masterId) {
        MasterProfileEntity master = masterProfileRepository.findById(masterId)
                .orElseThrow(() -> new ItemNotFoundException("Usta topilmadi"));

        ProfileEntity profile = profileRepository.findById(masterId)
                .orElseThrow(() -> new ItemNotFoundException("Usta topilmadi"));

        // 2. Ustaning jami tamomlagan ishlari sonini hisoblaymiz (Mijoz ishonchi uchun)
        Long completedCount = orderRepository.countByMasterIdAndStatus(masterId, OrderStatusEnum.COMPLETED);

        // 3. Ustaning lokatsiya DTO sini tayyorlaymiz (Aniq koordinatalar berilmaydi!)
        LocationDTO location = new LocationDTO(
                profile.getRegion(),
                profile.getCity(),
                null, // lat yashirin
                null  // lon yashirin
        );

        // 4. Ustaning kasbiy ma'lumotlari
        ProfessionalDTO profInfo = new ProfessionalDTO(
                master.getBio(),
                master.getMasterCategories().stream()
                        .map(mc -> mc.getCategory().getNameUz())
                        .toList()
        );

        // Faqat pastki xizmatlarni (sub-category) olish uchun parent'i null bo'lmaganlarini filtrlaymiz
        List<MasterCategoryEntity> masterCategories = masterCategoryRepository.findAllByMasterProfileIdAndCategoryParentIsNotNull(masterId);

        List<MasterServicePriceDTO> servicePriceDTOs = masterCategories.stream().map(mc ->
                MasterServicePriceDTO.builder()
                        .id(mc.getId())
                        .subCategoryId(mc.getCategoryId())
                        .subCategoryName(mc.getCategory().getNameUz())
                        .priceType(mc.getPriceType())
                        .minPrice(mc.getMinPrice())
                        .maxPrice(mc.getMaxPrice())
                        .build()
        ).collect(Collectors.toList());

        // 6. Ustaning faol ish kunlari jadvalini olamiz
        List<MasterWorkDayEntity> workDays = masterWorkDayRepository.findAllByMasterProfileId(masterId);

        List<MasterWorkDayDTO> scheduleDTOs = workDays.stream().map(wd -> {
            MasterWorkDayDTO dayDto = new MasterWorkDayDTO();
            dayDto.setDayOfWeek(wd.getDayOfWeek());
            dayDto.setWorkStart(wd.getWorkStart());
            dayDto.setWorkEnd(wd.getWorkEnd());
            dayDto.setIsActive(true); // Bazada bor kunlar tabiatdan faol kunlar hisoblanadi
            return dayDto;
        }).collect(Collectors.toList());

        //Avatar url
        String avaUrl = (profile.getAvatarAttachId() != null ? attachService.openURL(profile.getAvatarAttachId()) : null);

        // 7. Hamma ma'lumotni bitta Public DTO ga jamlab frontendga uchiramiz
        return MasterPublicProfileDTO.builder()
                .id(master.getId())
                .fullName(profile.getFullName())
                .avatarUrl(avaUrl)
                .rating(master.getRating() != null ? master.getRating() : 5.0)
                .completedOrdersCount(completedCount)
                .location(location)
                .profInfo(profInfo)
                .services(servicePriceDTOs)
                .schedule(scheduleDTOs)
                .build();
    }
}
