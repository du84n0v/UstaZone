package uz.ustazone.dto.profile.general;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailChangeRequestDTO {
    @NotBlank(message = "Yangi email kiriting")
    @Email(message = "Email formati noto'g'ri")
    private String newEmail;
}