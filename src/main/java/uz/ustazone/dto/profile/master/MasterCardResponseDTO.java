package uz.ustazone.dto.profile.master;

import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterCardResponseDTO {
    private String id;
    private String fullName;
    private String avatarUrl;
    private String city;
    private String region;
    private Double rating;
    private Long completedOrdersCount;
    private List<String> categories; // Masalan: ["Santexnik", "Elektrik"]
}