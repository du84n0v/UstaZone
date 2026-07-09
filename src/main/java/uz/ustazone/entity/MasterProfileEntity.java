package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.VerificationTierEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "master_profile")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterProfileEntity {

    @Id
    @Column(name = "profile_id")
    private String id;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", nullable = false, unique = true)
    private ProfileEntity profile;

    @Column(name = "bio", columnDefinition = "text")
    private String bio;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(name = "rating")
    private Double rating = 0.0;

    @Column(name = "jobs_completed")
    private Integer jobsCompleted = 0;

    @Column(name = "is_verified")
    private Boolean isVerified = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_tier")
    private VerificationTierEnum verificationTier = VerificationTierEnum.BASIC;

    @Column(name = "balance")
    private BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "withdrawable_balance")
    private BigDecimal withdrawableBalance = BigDecimal.ZERO;

    @OneToMany(mappedBy = "masterProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MasterCategoryEntity> masterCategories = new ArrayList<>();

    @Column(name = "service_radius_km")
    private Integer serviceRadiusKm;

    @OneToMany(mappedBy = "masterProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MasterWorkDayEntity> workDays = new ArrayList<>();

    @Column(name = "accepts_emergency")
    private Boolean acceptsEmergency = false;

    // ---- SENSITIVE INFORMATION  ----
    @Column(name = "id_document_attach_id")
    private String idDocumentAttachId;

    @Column(name = "selfie_attach_id")
    private String selfieAttachId;

    @OneToMany(mappedBy = "masterProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MasterCertificateEntity> certificates = new ArrayList<>();

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "bank_account")
    private String bankAccount;

    @Column(name = "tax_info")
    private String taxInfo;
    // -----------------------------------------------

    @Column(name = "visible")
    private Boolean visible = true;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}