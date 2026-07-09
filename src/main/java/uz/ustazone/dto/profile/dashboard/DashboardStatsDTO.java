package uz.ustazone.dto.profile.dashboard;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private Long todayOrdersCount;       // Bugungi orderlar
    private Long newOrdersCount;         // Yangi
    private Long inProgressOrdersCount;  // Jarayonda
    private Double todayEarnings;        // Bugungi daromad (so'mda)
    
    private Long completedOrdersCount;   // Bajarilgan ishlar
    private Double rating;               // Reyting (masalan: 4.8)
    private Double balance;              // Balans (so'mda)
    private Long withdrawalsCount;       // Yechib olishlar soni
}