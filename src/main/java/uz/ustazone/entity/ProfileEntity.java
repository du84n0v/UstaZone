package uz.ustazone.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.GeneralStatusEnum;
import uz.ustazone.enums.ProfileRoleEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "profile")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProfileEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "email", nullable = false, unique = true)
    @Email
    private String email;

    @Column(name = "phone", unique = true)
    private String phone;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private ProfileRoleEnum role;

    @Column(name = "region")
    private String region;

    @Column(name = "city")
    private String city;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Column(name = "is_blocked")
    private Boolean isBlocked = false;

    @Column(name = "bonus_balance")
    private Double bonusBalance = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GeneralStatusEnum status;

    @Column(name = "avatar_attach_id")
    private String avatarAttachId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avatar_attach_id", insertable = false, updatable = false)
    private AttachEntity avatar;

    @Column(name = "visible")
    private Boolean visible = true;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}