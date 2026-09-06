package uz.ustazone.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Control PromoCode API", description = "API list for admin to control PromoCode")
public class AdminPromoCodeController {

    @Autowired
    private AdminPromoCodeService promoCodeService;

    @PostMapping
    @Operation(summary = "Post", description = "API used for creating new PromoCode")
    public ResponseEntity<AdminPromoCodeResponseDTO> create(@Valid @RequestBody AdminPromoCodeCreateDTO dto) {
        return ResponseEntity.ok(promoCodeService.create(dto));
    }

    @GetMapping
    @Operation(summary = "Get", description = "API used for getting all promocode")
    public ResponseEntity<List<AdminPromoCodeResponseDTO>> getAll() {
        return ResponseEntity.ok(promoCodeService.getAll());
    }

    @PatchMapping("/{id}/toggle")
    @Operation(summary = "Put", description = "API used for toggling one Promocode(make avtive/inactive")
    public ResponseEntity<AdminPromoCodeResponseDTO> toggleStatus(@PathVariable String id) {
        return ResponseEntity.ok(promoCodeService.toggleStatus(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete", description = "API used for deleting one PromoCode")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        promoCodeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}