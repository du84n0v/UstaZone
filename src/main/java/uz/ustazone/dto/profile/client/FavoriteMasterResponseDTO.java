package uz.ustazone.dto.profile.client;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteMasterResponseDTO {
    private String masterId;
    private String fullName;
    private String phone;
    private String address;
    private String region;
    private String avatarUrl;
    private LocalDateTime savedDate;
}