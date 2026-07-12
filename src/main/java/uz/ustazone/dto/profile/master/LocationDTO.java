package uz.ustazone.dto.profile.master;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LocationDTO {
    private String region;
    private String city;
    private Double latitude;
    private Double longitude;
}