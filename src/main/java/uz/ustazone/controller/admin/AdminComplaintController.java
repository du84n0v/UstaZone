package uz.ustazone.controller.admin;

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
public class AdminComplaintController {

    @Autowired
    private AdminComplaintService adminComplaintService;

    // 1. Tablar va pagination bo'yicha ro'yxatni olish
    @GetMapping
    public ResponseEntity<Page<AdminComplaintViewDTO>> getComplaints(
            @RequestParam(defaultValue = "OPEN") ComplaintTabEnum tab,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(adminComplaintService.getComplaints(tab, page, size));
    }

    // 2. Bitta shikoyatni ID orqali to'liq ko'rish
    @GetMapping("/{id}")
    public ResponseEntity<AdminComplaintResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(adminComplaintService.getById(id));
    }

    // 3. Shikoyatni hal qilish (Ko'k tugma: "Hal qilish")
    @PutMapping("/{id}/resolve")
    public ResponseEntity<String> resolveComplaint(
            @PathVariable String id,
            @RequestBody(required = false) AdminComplaintActionDTO dto
    ) {
        return ResponseEntity.ok(adminComplaintService.resolve(id, dto));
    }

    // 4. Shikoyatni rad etish (Qora tugma: "Rad etish")
    @PutMapping("/{id}/reject")
    public ResponseEntity<String> rejectComplaint(
            @PathVariable String id,
            @RequestBody(required = false) AdminComplaintActionDTO dto
    ) {
        return ResponseEntity.ok(adminComplaintService.reject(id, dto));
    }
}