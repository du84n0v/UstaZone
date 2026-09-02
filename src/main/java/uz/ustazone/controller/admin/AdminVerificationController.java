package uz.ustazone.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.profile.admin.AdminVerificationDetailResponseDTO;
import uz.ustazone.dto.profile.admin.AdminVerificationRejectRequestDTO;
import uz.ustazone.dto.profile.admin.AdminVerificationViewDTO;
import uz.ustazone.enums.VerificationStatusEnum;
import uz.ustazone.service.profile.admin.AdminVerificationService;

@RestController
@RequestMapping("api/v1/admin/verification")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin User API", description = "API list for verify masters")
public class AdminVerificationController {

    @Autowired
    private AdminVerificationService adminVerificationService;

    @GetMapping
    @Operation(summary = "Get", description = "API used for getting masters by status")
    public ResponseEntity<Page<AdminVerificationViewDTO>> getMasters(@RequestParam(required = false, defaultValue = "PENDING")VerificationStatusEnum status,
                                                                     @RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(adminVerificationService.getApplicationsByStatus(status, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get", description = "API used for getting Master detail infos")
    public ResponseEntity<AdminVerificationDetailResponseDTO> getApplicationById(@PathVariable String id){
        return ResponseEntity.ok(adminVerificationService.getApplicationById(id));
    }

    @PutMapping("/{id}/approve")
    @Operation(summary = "Put", description = "API used for approve master application")
    public ResponseEntity<String> approveApplication(@PathVariable String id){
        return ResponseEntity.ok(adminVerificationService.approveApplication(id));
    }

    @PutMapping("/reject")
    @Operation(summary = "Put", description = "API used for reject master application")
    public ResponseEntity<String> rejectApplication(@RequestParam AdminVerificationRejectRequestDTO dto){
        return ResponseEntity.ok(adminVerificationService.rejectApplication(dto));
    }

}
