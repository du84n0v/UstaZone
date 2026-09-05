package uz.ustazone.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.profile.admin.AdminComplaintActionDTO;
import uz.ustazone.dto.profile.admin.AdminComplaintResponseDTO;
import uz.ustazone.dto.profile.admin.AdminComplaintViewDTO;
import uz.ustazone.enums.ComplaintTabEnum;
import uz.ustazone.service.profile.admin.AdminComplaintService;

@RestController
@RequestMapping("/api/v1/admin/complaints")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin API", description = "API list for admin to control complains")
public class AdminComplaintController {

    @Autowired
    private AdminComplaintService adminComplaintService;

    // 1. Tablar va pagination bo'yicha ro'yxatni olish
    @GetMapping
    @Operation(summary = "Get", description = "API used for getting complains based on type")
    public ResponseEntity<Page<AdminComplaintViewDTO>> getComplaints(
            @RequestParam(defaultValue = "OPEN") ComplaintTabEnum tab,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminComplaintService.getComplaints(tab, page, size));
    }

    // 2. Bitta shikoyatni ID orqali to'liq ko'rish
    @GetMapping("/{id}")
    @Operation(summary = "Get", description = "API used for getting one complain details")
    public ResponseEntity<AdminComplaintResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(adminComplaintService.getById(id));
    }

    // 3. Shikoyatni hal qilish (Ko'k tugma: "Hal qilish")
    @PutMapping("/{id}/resolve")
    @Operation(summary = "Put", description = "API used for resolving complain")
    public ResponseEntity<String> resolveComplaint(
            @PathVariable String id,
            @RequestBody(required = false) AdminComplaintActionDTO dto
    ) {
        return ResponseEntity.ok(adminComplaintService.resolve(id, dto));
    }

    // 4. Shikoyatni rad etish (Qora tugma: "Rad etish")
    @PutMapping("/{id}/reject")
    @Operation(summary = "Put", description = "API used for rejecting complain")
    public ResponseEntity<String> rejectComplaint(
            @PathVariable String id,
            @RequestBody(required = false) AdminComplaintActionDTO dto
    ) {
        return ResponseEntity.ok(adminComplaintService.reject(id, dto));
    }
}