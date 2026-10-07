package uz.ustazone.controller.general;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.entity.MasterProfileEntity;
import uz.ustazone.repository.MasterProfileRepository;
import uz.ustazone.util.SpringSecurityUtil;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/payments")
@Tag(name = "Payment API", description = "To'lovlar va balansni to'ldirish")
public class PaymentController {

    @Autowired
    private MasterProfileRepository masterProfileRepository;

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/fill-balance-test")
    @Operation(summary = "Fill Balance (Test)", description = "Ustalar o'z balansini to'ldirib ko'rishi uchun test API (Payme/Click integratsiyasigacha)")
    public ResponseEntity<String> fillBalance(@RequestParam BigDecimal amount) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        
        MasterProfileEntity master = masterProfileRepository.findByProfileId(masterId)
                .orElseThrow(() -> new RuntimeException("Usta profili topilmadi"));
                
        // Balansga pul qo'shamiz
        master.setBalance((master.getBalance() != null ? master.getBalance() : BigDecimal.ZERO).add(amount));
        masterProfileRepository.save(master);
        
        return ResponseEntity.ok("Balans muvaffaqiyatli to'ldirildi! Yangi balans: " + master.getBalance());
    }
}