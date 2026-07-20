package uz.ustazone.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.auth.*;
import uz.ustazone.dto.profile.master.MasterResponseDTO;
import uz.ustazone.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Auth API", description = "API list for authorization")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register/master")
    @Operation(summary = "Register", description = "API used for register only Master")
    ResponseEntity<String> registerMaster(@Valid @RequestBody MasterRegisterDTO dto) {
        return ResponseEntity.ok(authService.registerMaster(dto));
    }

    @PostMapping("/register/client")
    @Operation(summary = "Register", description = "API used for register only Client")
    ResponseEntity<String> registerClient(@Valid @RequestBody ClientRegisterDTO dto) {
        return ResponseEntity.ok(authService.registerClient(dto));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify", description = "API used for verify email after registration")
    public ResponseEntity<String> verify(@Valid @RequestBody VerificationDTO dto) {
        return ResponseEntity.ok(authService.verify(dto));
    }

    @PostMapping("/resend-code")
    @Operation(summary = "Resend-code", description = "API used for resend email code")
    public ResponseEntity<String> resendCode(@Valid @RequestBody ResendCodeDTO dto) {
        return ResponseEntity.ok(authService.resendCode(dto));
    }

    @PostMapping("/login")
    @Operation(summary = "Login", description = "API used for login")
    public ResponseEntity<MasterResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }
}
