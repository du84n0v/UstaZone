package uz.ustazone.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.profile.admin.AdminMasterResponseDTO;
import uz.ustazone.service.profile.admin.AdminMasterService;
import uz.ustazone.util.PageUtil;

@RestController
@RequestMapping("/api/v1/admin/masters")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminMasterController {

    @Autowired
    private AdminMasterService adminMasterService;

    // 1. Ustalarni qidirish va sahifalab olish (Barcha ustalar jadvali uchun)
    @GetMapping
    public ResponseEntity<Page<AdminMasterResponseDTO>> getMasters(
            @RequestParam(required = false) String search, // Ism yoki telefon bo'yicha qidiruv
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AdminMasterResponseDTO> masters = adminMasterService.getMastersList(search, PageUtil.page(page), size);
        return ResponseEntity.ok(masters);
    }

    // 3. Ustani rad etish / bloklash
    @PutMapping("/{masterId}/reject")
    public ResponseEntity<Void> rejectMaster(@PathVariable String masterId) {
        adminMasterService.rejectMaster(masterId);
        return ResponseEntity.ok().build();
    }
}