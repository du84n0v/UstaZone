package uz.ustazone.service.profile.admin;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uz.ustazone.dto.profile.admin.AdminDashboardStatsDTO;
import uz.ustazone.enums.ComplaintStatusEnum;
import uz.ustazone.repository.ProfileRepository;
import uz.ustazone.repository.OrderRepository;
import uz.ustazone.repository.ComplaintRepository; // Agar shikoyatlar repozitoriyasi bo'lsa
import uz.ustazone.enums.ProfileRoleEnum; // User rollari uchun enum (ROLE_CLIENT, MASTER, ADMIN)

import java.math.BigDecimal;

@Service
public class AdminDashboardService {

    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ComplaintRepository complaintRepository;

    @Transactional(readOnly = true)
    public AdminDashboardStatsDTO getDashboardStats() {
        // 1. Jami ustalar soni (Roli MASTER bo'lgan profillar)
        long totalMasters = profileRepository.countByRole(ProfileRoleEnum.ROLE_MASTER);

        // 2. Jami buyurtmalar soni
        long totalOrders = orderRepository.count();

        // 3. Jami foydalanuvchilar soni (barcha roldagilar)
        long totalUsers = profileRepository.count();

        // 4. Jami faol munozaralar / shikoyatlar soni
        long totalDisputes = complaintRepository.countByStatus(ComplaintStatusEnum.OPEN); // yoki o'zingizning holat (status) bo'yicha

        // 5. Jami yig'ilgan komissiya summasi
        // (Agar bazada buyurtmalar hali bo'lmasa sum() null qaytaradi, shuning uchun coalesce ishlatamiz)
        BigDecimal totalCommission = orderRepository.sumTotalCommission();
        if (totalCommission == null) {
            totalCommission = BigDecimal.ZERO;
        }

        return new AdminDashboardStatsDTO(
            totalMasters,
            totalOrders,
            totalUsers,
            totalDisputes,
            totalCommission
        );
    }
}