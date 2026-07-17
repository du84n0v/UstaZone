package uz.ustazone.dto.profile.general;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerifyRequestDTO {
    @NotBlank
    private String newEmail;
    @NotBlank
    private String code;
}