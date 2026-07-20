package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import uz.ustazone.enums.BillingPeriodEnum;
import uz.ustazone.enums.SubscriptionAudienceEnum;
import uz.ustazone.enums.SubscriptionTierEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "subscription")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // --- USER BOG'LANISHI (UNIQUE) ---
    @Column(name = "user_id", nullable = false, unique = true)
    private String userId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private ProfileEntity user;

    // --- OBUNA DETALLARI ---
    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false)
    private SubscriptionTierEnum tier; // FREE, BASIC, PRO va h.k.

    @Enumerated(EnumType.STRING)
    @Column(name = "audience", nullable = false)
    private SubscriptionAudienceEnum audience; // ROLE_CLIENT, MASTER

    @Enumerated(EnumType.STRING)
    @Column(name = "billing_period", nullable = false)
    private BillingPeriodEnum billingPeriod; // MONTHLY, YEARLY

    // --- STATUS VA MUDDATLAR ---
    @Column(name = "is_trial")
    @Builder.Default
    private Boolean isTrial = false;

    @Column(name = "trial_ends_date")
    private LocalDateTime trialEndsDate;

    @Column(name = "expires_date", nullable = false)
    private LocalDateTime expiresDate;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}