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
import uz.ustazone.repository.*;
import uz.ustazone.service.AttachService;

import java.math.BigDecimal;
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

    public ClientCabinetResponseDTO getClientCabinetInfo(String clientId) {
        ProfileEntity client = profileRepository.findById(clientId)
                .orElseThrow(() -> new ItemNotFoundException("Mijoz topilmadi"));

        // 1. Jami buyurtmalar soni
        Long totalOrders = orderRepository.countByClientId(clientId);

        // 2. Faol buyurtmalar statuslari (Mijoz uchun hali tugallanmagan jarayon)
        List<OrderStatusEnum> activeStatuses = List.of(
                OrderStatusEnum.PENDING,
                OrderStatusEnum.ACCEPTED,
                OrderStatusEnum.EN_ROUTE,
                OrderStatusEnum.IN_PROGRESS
        );
        Long activeCount = orderRepository.countByClientIdAndStatusIn(clientId, activeStatuses);

        // 3. Bajarilgan buyurtmalar statuslari
        List<OrderStatusEnum> completedStatuses = List.of(
                OrderStatusEnum.COMPLETED,
                OrderStatusEnum.CONFIRMED,
                OrderStatusEnum.PAID,
                OrderStatusEnum.REVIEWED
        );
        Long completedCount = orderRepository.countByClientIdAndStatusIn(clientId, completedStatuses);

        //clientning orderlari
        List<OrderEntity> activeOrderEntities = orderRepository.findAllByClientIdAndStatusInOrderByCreatedDateDesc(clientId, activeStatuses);
        List<OrderShortResponseDTO> activeOrderDTOs = activeOrderEntities.stream().map(order ->
                OrderShortResponseDTO.builder()
                        .id(order.getId())
                        .title(order.getTitle())
                        .amount(order.getAmount())
                        .status(order.getStatus().name())
                        .createdDate(order.getCreatedDate())
                        .categories(order.getCategories().stream()
                                .map(oc -> new CategoryShortInfoDTO(oc.getCategoryId(), oc.getCategory().getNameUz())) // Kategoriyalar nomlari listi
                                .toList())
                        .build()
        ).toList();

        //address
        List<ClientAddressEntity> addresses = clientAddressRepository.findAllByClientId(clientId);
        List<ClientAddressResponseDTO> addressRes = addresses.stream()
                .map(ca -> {
                    ClientAddressResponseDTO dto = new ClientAddressResponseDTO();
                    dto.setRegion(ca.getRegion());
                    dto.setCity(ca.getCity());
                    dto.setAddressLine(ca.getAddressLine());
                    dto.setType(ca.getAddressType());

                    return dto;})
                .toList();

        // DTO'ni yig'ib qaytaramiz
        return ClientCabinetResponseDTO.builder()
                .id(client.getId())
                .fullName(client.getFullName())
                .bonusBalance(client.getBonusBalance() != null ? client.getBonusBalance() : BigDecimal.ZERO)
                .totalOrdersCount(totalOrders)
                .activeOrdersCount(activeCount)
                .completedOrdersCount(completedCount)
                .activeOrders(activeOrderDTOs)
                .addresses(addressRes)
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
