package uz.ustazone.dto.profile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CurrentProfileDTO {
    private String id;
    private String fullName;
    private String email;
    private String role;
}
