package uz.ustazone.controller.admin;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.profile.admin.AdminPromoCodeCreateDTO;
import uz.ustazone.dto.profile.admin.AdminPromoCodeResponseDTO;
import uz.ustazone.service.profile.admin.AdminPromoCodeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/promocodes")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPromoCodeController {

    @Autowired
    private AdminPromoCodeService promoCodeService;

    @PostMapping
    public ResponseEntity<AdminPromoCodeResponseDTO> create(@Valid @RequestBody AdminPromoCodeCreateDTO dto) {
        return ResponseEntity.ok(promoCodeService.create(dto));
    }

    @GetMapping
    public ResponseEntity<List<AdminPromoCodeResponseDTO>> getAll() {
        return ResponseEntity.ok(promoCodeService.getAll());
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<AdminPromoCodeResponseDTO> toggleStatus(@PathVariable String id) {
        return ResponseEntity.ok(promoCodeService.toggleStatus(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        promoCodeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}