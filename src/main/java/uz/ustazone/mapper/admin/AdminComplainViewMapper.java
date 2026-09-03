package uz.ustazone.mapper.admin;

import uz.ustazone.enums.ComplaintCategoryEnum;
import uz.ustazone.enums.ComplaintStatusEnum;

import java.time.LocalDateTime;

public interface AdminComplainViewMapper {
    String getId();
    ComplaintCategoryEnum getCategory();
    String getDescription();
    ComplaintStatusEnum getStatus();
    String getAdminNote();
    String getOrderId();

    // Shikoyatchi (Mijoz)
    String getReporterId();
    String getReporterFullName();
    String getReporterPhone();

    // Shikoyat qilingan (Usta)
    String getTargetId();
    String getTargetFullName();
    String getTargetPhone();

    LocalDateTime getCreatedDate();
    LocalDateTime getResolvedDate();
}
