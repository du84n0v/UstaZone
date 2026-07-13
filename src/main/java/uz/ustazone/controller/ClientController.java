package uz.ustazone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.ustazone.dto.profile.ClientCabinetResponseDTO;
import uz.ustazone.dto.profile.MasterPublicProfileDTO;
import uz.ustazone.service.ClientService;
import uz.ustazone.util.SpringSecurityUtil;

@RestController
@RequestMapping("/api/v1/client")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/cabinet")
    public ResponseEntity<ClientCabinetResponseDTO> getClientCabinet() {
        String clientId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(clientService.getClientCabinetInfo(clientId));
    }

    @GetMapping("/master/{masterId}/public-profile")
    public ResponseEntity<MasterPublicProfileDTO> getMasterPublicProfile(@PathVariable String masterId) {
        MasterPublicProfileDTO profile = clientService.getMasterPublicProfileForClient(masterId);
        return ResponseEntity.ok(profile);
    }
}
