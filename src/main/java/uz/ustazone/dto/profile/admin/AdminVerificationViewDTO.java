package uz.ustazone.dto.profile.admin;

import lombok.*;
import uz.ustazone.enums.VerificationStatusEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminVerificationViewDTO {
    private String id;
    private String masterId;
    private String fullName;
    private String phone;
    private VerificationStatusEnum status;
    private LocalDateTime createdDate;
}
