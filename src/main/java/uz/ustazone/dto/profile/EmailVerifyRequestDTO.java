package uz.ustazone.dto.profile;

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