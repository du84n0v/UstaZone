package uz.ustazone.dto.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class MasterResponseDTO {
    private String accessToken;
    private String refreshToken;
    private CurrentProfileDTO profile; // Tizimga kirgan foydalanuvchi ma'lumotlari
    private String avatarUrl;
}