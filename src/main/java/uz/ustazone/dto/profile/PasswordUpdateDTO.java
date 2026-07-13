package uz.ustazone.dto.profile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdateDTO {
    @NotBlank(message = "Eski parol bo'sh bo'lmasligi kerak")
    private String oldPassword;

    @NotBlank(message = "Yangi parol bo'sh bo'lmasligi kerak")
    @Size(min = 6, message = "Parol kamida 6 ta belgidan iborat bo'lsin")
    private String newPassword;
}