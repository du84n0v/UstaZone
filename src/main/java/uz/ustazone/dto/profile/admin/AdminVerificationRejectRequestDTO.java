package uz.ustazone.dto.profile.admin;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminVerificationRejectRequestDTO {
    private String id;
    private String reason;
}
