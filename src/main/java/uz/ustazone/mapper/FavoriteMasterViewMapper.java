package uz.ustazone.mapper;

import java.time.LocalDateTime;

public interface FavoriteMasterViewMapper {
    String getMasterId();
    String getFullName();
    String getPhone();
    String getAddress();
    String getRegion();
    String getAvatarAttachId();
    LocalDateTime getSavedDate();
}