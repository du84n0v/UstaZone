package uz.ustazone.controller.general;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.ustazone.dto.profile.general.ProfileBalanceDTO;
import uz.ustazone.service.general.ProfileBalanceService;

@RestController
@RequestMapping("/api/v1/profile")
@Tag(name = "Profile API", description = "Foydalanuvchi profili uchun API (Balans va h.k)")
public class ProfileController {

    @Autowired
    private ProfileBalanceService profileBalanceService;

    @PreAuthorize("hasAnyRole('MASTER', 'CLIENT')")
    @GetMapping("/me/balance")
    @Operation(summary = "Get Balance", description = "Joriy foydalanuvchining hamyon balansini olish")
    public ResponseEntity<ProfileBalanceDTO> getMyBalance() {
        return ResponseEntity.ok(profileBalanceService.getMyBalance());
    }
}