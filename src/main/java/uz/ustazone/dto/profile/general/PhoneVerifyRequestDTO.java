package uz.ustazone.dto.profile.general;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneVerifyRequestDTO {
    @NotBlank
    private String newPhone;
    @NotBlank
    private String code;
}