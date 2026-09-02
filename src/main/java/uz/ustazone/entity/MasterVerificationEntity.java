package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.VerificationStatusEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "master_verification")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MasterVerificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_profile_id", nullable = false)
    private ProfileEntity profile;

    @Column(name = "passport_attach_id", nullable = false)
    private String passportAttachId;

    @Column(name = "selfie_attach_id", nullable = false)
    private String selfieAttachId;

    // Sertifikat attach ID lari uchun eng qulay usul
    @ElementCollection
    @CollectionTable(name = "verification_certificates", joinColumns = @JoinColumn(name = "verification_id"))
    @Column(name = "certificate_attach_id")
    @Builder.Default
    private List<String> certificateIds = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private VerificationStatusEnum status = VerificationStatusEnum.PENDING;

    @Column(name = "reject_reason")
    private String rejectReason;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "review_date")
    private LocalDateTime reviewDate;
}
