package uz.ustazone.dto.profile.master;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RecentActivityDTO {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdAt;
}