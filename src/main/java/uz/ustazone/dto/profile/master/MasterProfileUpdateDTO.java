package uz.ustazone.dto.profile.master;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MasterProfileUpdateDTO {
    @NotBlank
    private String fullName;
    @NotBlank
    private String avatarAttachId;
    @NotBlank
    private String region;
    @NotBlank
    private String city;
    @NotBlank
    private Double latitude;
    @NotBlank
    private Double longitude;
    @NotBlank
    private String bio;
    @NotBlank
    private Integer searchRadius;
    @NotEmpty
    private List<String> skills;
}