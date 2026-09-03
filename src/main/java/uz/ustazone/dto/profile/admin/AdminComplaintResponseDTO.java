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
public class AdminComplaintResponseDTO {
    private AdminComplaintViewDTO viewDto;
    private List<String> attachUrls;
}
