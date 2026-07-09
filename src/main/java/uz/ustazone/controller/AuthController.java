package uz.ustazone.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uz.ustazone.dto.auth.*;
import uz.ustazone.dto.profile.MasterResponseDTO;
import uz.ustazone.service.AuthService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping(value = "/register/master", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<String> registerMaster(@Valid
                                    @RequestPart("data") MasterRegisterDTO dto,
                                    @RequestPart(value = "passportImg", required = false) MultipartFile passportImg,
                                    @RequestPart(value = "selfieImg", required = false) MultipartFile selfieImg,
                                    @RequestPart(value = "certificates", required = false) List<MultipartFile> certificates,
                                    @RequestPart(value = "profileImg", required = false) MultipartFile profileImg){
        return ResponseEntity.ok(authService.registerMaster(
                dto,
                passportImg,
                selfieImg,
                certificates,
                profileImg));
    }

    @PostMapping("/register/client")
    ResponseEntity<String> registerClient(@Valid @RequestBody ClientRegisterDTO dto){
        return ResponseEntity.ok(authService.registerClient(dto));
    }

    @PostMapping("/verify")
    public ResponseEntity<String> verify(@Valid @RequestBody VerificationDTO dto) {
        return ResponseEntity.ok(authService.verify(dto));
    }

    @PostMapping("/resend-code")
    public ResponseEntity<String> resendCode(@Valid @RequestBody ResendCodeDTO dto){
        return ResponseEntity.ok(authService.resendCode(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<MasterResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto){
        return ResponseEntity.ok(authService.login(dto));
    }
}
