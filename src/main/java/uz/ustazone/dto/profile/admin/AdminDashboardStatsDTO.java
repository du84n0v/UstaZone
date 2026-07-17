package uz.ustazone.dto.profile.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminDashboardStatsDTO{
    private Long totalMasters;       // Jami ustalar soni
    private Long totalOrders;       // Jami buyurtmalar soni
    private Long totalUsers;        // Jami foydalanuvchilar (Mijoz + Usta + Admin)
    private Long totalDisputes;      // Munozaralar (Shikoyatlar) soni
    BigDecimal totalCommission; // Jami yig'ilgan komissiya summasi
}