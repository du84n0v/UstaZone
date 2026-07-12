package uz.ustazone.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PhoneChangeRequestDTO {
    @NotBlank(message = "Yangi telefon raqam kiriting")
    @Pattern(regexp = "^\\+998\\d{9}$", message = "Telefon raqam formati noto'g'ri")
    private String newPhone;
}