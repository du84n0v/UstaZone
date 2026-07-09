package uz.ustazone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.ustazone.dto.profile.dashboard.DashboardResponse;
import uz.ustazone.service.MasterDashboardService;
import uz.ustazone.util.SpringSecurityUtil;

@RestController
@RequestMapping("/api/v1/master")
@PreAuthorize("hasRole('MASTER')") // Faqat MASTER rolidagi usta tokeni bilan kirsa ishlaydi
public class MasterController {

    @Autowired
    private MasterDashboardService dashboardService;

    @GetMapping("/dashboard")
    public ResponseEntity<DashboardResponse> getDashboardData() {
        String masterId = SpringSecurityUtil.getCurrentProfileId();

        DashboardResponse response = dashboardService.getDashboard(masterId);
        
        return ResponseEntity.ok(response);
    }
}