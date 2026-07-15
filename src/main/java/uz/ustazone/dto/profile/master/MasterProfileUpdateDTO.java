package uz.ustazone.dto.profile.master;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    @NotBlank
    private String bio;
    @NotNull
    private Integer searchRadius;
    @NotEmpty
    private List<String> skills;
}