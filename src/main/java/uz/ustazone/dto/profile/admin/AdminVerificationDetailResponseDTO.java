package uz.ustazone.dto.profile.admin;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AdminVerificationDetailResponseDTO {

    private AdminVerificationViewDTO viewDto;

    private String passportAttachId;
    private String selfieAttachId;
    private List<String> certificates;

    private String rejectReason;
}
