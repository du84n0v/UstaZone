package uz.ustazone.dto.profile.admin;

import uz.ustazone.enums.GeneralStatusEnum;
import uz.ustazone.enums.ProfileRoleEnum;

import java.time.LocalDate;

public record AdminUserResponseDTO(
    String id,
    String fullName,
    String phone,
    String city,
    ProfileRoleEnum role,
    GeneralStatusEnum status,
    Boolean isBlocked,
    LocalDate createdDate
) {}