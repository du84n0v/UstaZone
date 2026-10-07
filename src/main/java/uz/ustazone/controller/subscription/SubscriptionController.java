package uz.ustazone.controller.subscription;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.subscription.SubscriptionActivateDTO;
import uz.ustazone.service.subscription.SubscriptionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/subscriptions")
@Tag(name = "Subscription API", description = "Tarif va Obunalar")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/activate")
    @Operation(summary = "Activate Subscription", description = "Usta hamyonidagi puldan foydalanib obuna sotib olishi")
    public ResponseEntity<String> activate(@Valid @RequestBody SubscriptionActivateDTO dto) {
        return ResponseEntity.ok(subscriptionService.activate(dto));
    }
}