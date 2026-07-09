package uz.ustazone.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationDTO {
    @NotBlank
    private String email;
    @Size(min = 6, max = 6)
    private String code;
}