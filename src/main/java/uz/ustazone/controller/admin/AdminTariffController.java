package uz.ustazone.controller.admin;

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
public class AdminTariffController {

    @Autowired
    private AdminTariffService adminTariffService;

    @PostMapping
    public ResponseEntity<AdminTariffResponseDTO> create(@Valid @RequestBody AdminTariffRequestDTO dto) {
        return ResponseEntity.ok(adminTariffService.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminTariffResponseDTO> update(
            @PathVariable String id,
            @Valid @RequestBody AdminTariffRequestDTO dto
    ) {
        return ResponseEntity.ok(adminTariffService.update(id, dto));
    }

    @GetMapping
    public ResponseEntity<List<AdminTariffResponseDTO>> getAll(
            @RequestParam(required = false) TariffAudienceEnum audience
    ) {
        return ResponseEntity.ok(adminTariffService.getAll(audience));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminTariffResponseDTO> getById(@PathVariable String id) {
        return ResponseEntity.ok(adminTariffService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        adminTariffService.delete(id);
        return ResponseEntity.noContent().build();
    }
}