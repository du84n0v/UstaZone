package uz.ustazone.mapper.admin;

import uz.ustazone.enums.VerificationStatusEnum;

import java.time.LocalDateTime;

public interface AdminVerificationViewMapper {
    String getId();
    String getMasterId();
    String getFullName();
    String getPhone();
    VerificationStatusEnum getStatus();
    LocalDateTime getCreatedDate();
}
