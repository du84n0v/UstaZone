package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "promo_redemption")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoRedemptionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // --- PROMO CODE BOG'LANISHI ---
    @Column(name = "promo_code_id", nullable = false)
    private String promoCodeId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id", insertable = false, updatable = false)
    private PromoCodeEntity promoCode;

    // --- PROFILE (ROLE_CLIENT) BOG'LANISHI ---
    @Column(name = "profile_id", nullable = false)
    private String profileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    // --- ORDER BOG'LANISHI ---
    @Column(name = "order_id", nullable = false)
    private String orderId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private OrderEntity order;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}