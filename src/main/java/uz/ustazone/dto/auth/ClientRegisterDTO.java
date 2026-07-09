package uz.ustazone.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClientRegisterDTO {
    @NotBlank(message = "Ism familiya bosh bolmasligi kerak")
    private String fullName;
    @NotBlank(message = "Email bosh bolmasligi kerak")
    @Email(message = "Email togri formatda bolishi kerak")
    private String email;
    @NotBlank(message = "Phone bosh bolmasligi kerak")
    private String phone;
    @NotBlank(message = "Parol bo'sh bolishi mumkin emas")
    @Size(min = 5, max = 30, message = "Password uzunligi 5 va 30 orasiga bolishi kerak")
    private String password;
}
