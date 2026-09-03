package uz.ustazone.dto.profile.admin;

import lombok.*;
import uz.ustazone.enums.ComplaintCategoryEnum;
import uz.ustazone.enums.ComplaintStatusEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminComplaintViewDTO {
    private String id;
    private ComplaintCategoryEnum category;
    private String description;
    private ComplaintStatusEnum status;
    private String adminNote;
    private String orderId;

    // Shikoyatchi (Mijoz)
    private String reporterId;
    private String reporterFullName;
    private String reporterPhone;

    // Shikoyat qilingan (Usta)
    private String targetId;
    private String targetFullName;
    private String targetPhone;

    private LocalDateTime createdDate;
    private LocalDateTime resolvedDate;
}