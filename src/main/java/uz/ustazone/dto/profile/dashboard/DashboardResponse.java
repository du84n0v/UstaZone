package uz.ustazone.dto.profile.dashboard;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    
    private Boolean isPro;
    private Long proRemainingDays; // PRO status tugashiga qolgan kunlar
    
    private DashboardStatsDTO statistics; // 8 ta kartaning ma'lumotlari
    
    private List<EarningsChartDTO> earningsChart; // Grafik uchun ma'lumotlar ro'yxati
    
    private List<RecentActivityDTO> recentActivities; // So'nggi faoliyatlar ro'yxati
}