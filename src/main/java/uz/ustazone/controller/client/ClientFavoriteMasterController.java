package uz.ustazone.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.profile.client.FavoriteMasterResponseDTO;
import uz.ustazone.service.profile.client.ClientFavoriteMasterService;
import uz.ustazone.util.SpringSecurityUtil;

@RestController
@RequestMapping("/api/v1/client/favorites")
@PreAuthorize("hasRole('CLIENT')")
@Tag(name = "Favorite Master API", description = "API list for client favorite master crud")
public class ClientFavoriteMasterController {

    @Autowired
    private ClientFavoriteMasterService clientFavoriteService;

    // Yurakchani bosganda chaqiriladi
    @PostMapping("/{masterId}/toggle")
    @Operation(summary = "Post", description = "API used for add/delete master to/from favorite list")
    public ResponseEntity<String> toggleFavorite(@PathVariable String masterId) {
        String clientId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(clientFavoriteService.toggleFavorite(clientId, masterId));
    }

    // "Sevimlilar" tabini ochganda chaqiriladi
    @GetMapping
    @Operation(summary = "Get", description = "API used for getting client favorite masters")
    public ResponseEntity<Page<FavoriteMasterResponseDTO>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        String clientId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(clientFavoriteService.getFavorites(clientId, page, size));
    }

    // Usta sahifasida yurakcha holatini tekshirish
    @GetMapping("/{masterId}/check")
    @Operation(summary = "Get", description = "API used for checking it is favorite or not")
    public ResponseEntity<Boolean> isFavorite(@PathVariable String masterId) {
        String clientId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(clientFavoriteService.isFavorite(clientId, masterId));
    }
}