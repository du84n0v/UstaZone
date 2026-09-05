package uz.ustazone.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.address.ClientAddressRequestDTO;
import uz.ustazone.dto.address.ClientAddressResponseDTO;
import uz.ustazone.service.profile.client.ClientAddressService;
import uz.ustazone.util.SpringSecurityUtil;

import java.util.List;

@RestController
@RequestMapping("/api/v1/client/addresses")
@PreAuthorize("hasRole('CLIENT')")
@Tag(name = "Client API", description = "API list for client address crud")
public class ClientAddressController {

    @Autowired
    private ClientAddressService clientAddressService;

    @PostMapping
    @Operation(summary = "Post", description = "API used for creating new address for client")
    public ResponseEntity<ClientAddressResponseDTO> create(@Valid @RequestBody ClientAddressRequestDTO dto) {
        String currentUserId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(clientAddressService.create(currentUserId, dto));
    }

    @GetMapping
    @Operation(summary = "Get", description = "API used for getting all addresses")
    public ResponseEntity<List<ClientAddressResponseDTO>> getAll() {
        String currentUserId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(clientAddressService.getAll(currentUserId));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete", description = "API used for deleting client address")
    public ResponseEntity<Void> delete(@PathVariable String id) {

        String currentUserId = SpringSecurityUtil.getCurrentProfileId();
        clientAddressService.delete(currentUserId, id);
        return ResponseEntity.noContent().build();
    }
}