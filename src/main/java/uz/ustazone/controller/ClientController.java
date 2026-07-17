package uz.ustazone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.ustazone.dto.profile.client.ClientCabinetResponseDTO;
import uz.ustazone.dto.profile.master.MasterPublicProfileDTO;
import uz.ustazone.service.profile.client.ClientService;
import uz.ustazone.util.SpringSecurityUtil;

@RestController
@RequestMapping("/api/v1/client")
@Tag(name = "Client API", description = "API list for client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/cabinet")
    @Operation(summary = "Cabinet", description = "API used for get Client Cabinet")
    public ResponseEntity<ClientCabinetResponseDTO> getClientCabinet() {
        String clientId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(clientService.getClientCabinetInfo(clientId));
    }

    @GetMapping("/master/public-profile/{masterId}")
    @Operation(summary = "Cabinet", description = "API used for get Cabinet for everyone")
    public ResponseEntity<MasterPublicProfileDTO> getMasterPublicProfile(@PathVariable String masterId) {
        MasterPublicProfileDTO profile = clientService.getMasterPublicProfileForClient(masterId);
        return ResponseEntity.ok(profile);
    }
}
