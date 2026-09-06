package uz.ustazone.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.profile.admin.AdminTariffRequestDTO;
import uz.ustazone.dto.profile.admin.AdminTariffResponseDTO;
import uz.ustazone.enums.TariffAudienceEnum;
import uz.ustazone.service.profile.admin.AdminTariffService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/tariffs")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Control Tariff API", description = "API list for admin to control tariff")
public class AdminTariffController {

    @Autowired
    private AdminTariffService adminTariffService;

    @PostMapping
    @Operation(summary = "Post", description = "API used for creating new tariff")
    public ResponseEntity<AdminTariffResponseDTO> create(@Valid @RequestBody AdminTariffRequestDTO dto) {
        return ResponseEntity.ok(adminTariffService.create(dto));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Put", description = "API used for updating one tariff")
    public ResponseEntity<AdminTariffResponseDTO> update(
            @PathVariable String id,
            @Valid @RequestBody AdminTariffRequestDTO dto
    ) {
        return ResponseEntity.ok(adminTariffService.update(id, dto));
    }

    @GetMapping
    @Operation(summary = "Get", description = "API used for getting all tariff (optional giving who's tariff)")
    public ResponseEntity<List<AdminTariffResponseDTO>> getAll(
            @RequestParam(required = false) TariffAudienceEnum audience
    ) {
        return ResponseEntity.ok(adminTariffService.getAll(audience));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get", description = "API used for getting one tariff by id")
    public ResponseEntity<AdminTariffResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(adminTariffService.getById(id));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete", description = "API used for deleting one promocode")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        adminTariffService.delete(id);
        return ResponseEntity.noContent().build();
    }
}