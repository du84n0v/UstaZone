package uz.ustazone.controller.master;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.profile.master.MasterCardResponseDTO;
import uz.ustazone.dto.profile.master.MasterPublicProfileDTO;
import uz.ustazone.service.profile.master.PublicMasterService;

@RestController
@RequestMapping("/api/v1/public/masters")
@Tag(name = "Public Master API", description = "Barcha uchun ochiq ustalar ro'yxati va profili")
public class PublicMasterController {

    @Autowired
    private PublicMasterService publicMasterService;

    @GetMapping
    @Operation(summary = "Get Masters List", description = "Ustalar ro'yxati (kategoriya va shahar bo'yicha filter bilan)")
    public ResponseEntity<Page<MasterCardResponseDTO>> getMasters(
            @RequestParam(required = false) String categoryId,
            @RequestParam(required = false) String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(publicMasterService.getPublicMasters(categoryId, city, page, size));
    }

    @GetMapping("/{masterId}")
    @Operation(summary = "Get Master Public Profile", description = "Bitta ustaning to'liq ochiq profili (xizmat narxlari va ish jadvali)")
    public ResponseEntity<MasterPublicProfileDTO> getMasterProfile(@PathVariable String masterId) {
        return ResponseEntity.ok(publicMasterService.getMasterProfile(masterId));
    }
}