package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.SubscriptionAudienceEnum;
import uz.ustazone.enums.SubscriptionTierEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "subscription_plan")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SubscriptionPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "tier", nullable = false)
    private SubscriptionTierEnum tier; // FREE, BASIC, STANDARD, PRO, PREMIUM, VIP

    @Enumerated(EnumType.STRING)
    @Column(name = "audience", nullable = false)
    private SubscriptionAudienceEnum audience; // ROLE_CLIENT, MASTER

    @Column(name = "price", nullable = false)
    @Builder.Default
    private BigDecimal price = BigDecimal.ZERO;

    @Column(name = "duration_months", nullable = false)
    private Integer durationMonths;

    @Column(name = "max_services")
    private Integer maxServices; // Ustalar uchun xizmatlar soni limiti

    @Column(name = "ai_match_boost")
    private Integer aiMatchBoost; // AI qidiruvida ustunlik beruvchi ball

    @Column(name = "badge")
    private String badge; // Profil uchun vizual belgi (status)

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}