package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import uz.ustazone.enums.TariffAudienceEnum;
import uz.ustazone.enums.TariffTierEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tariffs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TariffEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Enumerated(EnumType.STRING)
    @Column(name = "audience", nullable = false)
    private TariffAudienceEnum audience; // MASTER yoki CLIENT

    @Column(name = "tier_code", nullable = false)
    private String tierCode; // basic, pro, vip, free

    @Enumerated(EnumType.STRING)
    @Column(name = "tier_name", nullable = false)
    private TariffTierEnum tierName; // BASIC, PRO, VIP, FREE

    @Column(name = "icon")
    private String icon; // 🟢, ⭐, 👑, FREE

    @Column(name = "badge")
    private String badge; // Masalan: "Yangi boshlovchilar", "Faol ustalar"

    // Narxlar (so'mda saqlash uchun Long eng qulay)
    @Column(name = "price_1_month", nullable = false)
    private BigDecimal price1Month;

    @Column(name = "price_3_month")
    private BigDecimal price3Month;

    @Column(name = "price_6_month")
    private BigDecimal price6Month;

    @Column(name = "price_12_month", nullable = false)
    private BigDecimal price12Month;

    // Har qatordagi xususiyatlar ro'yxati
    @Column(name = "features", columnDefinition = "TEXT")
    private String features;

    @Column(name = "sort_order", nullable = false)
    @Builder.Default
    private Integer sortOrder = 0; // Kartalarni ketma-ket chiqarish tartibi

    @Column(name = "is_popular", nullable = false)
    @Builder.Default
    private Boolean isPopular = false; // "Mashhur" switchi

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true; // "Faol" switchi

    @Column(name = "visible", nullable = false)
    @Builder.Default
    private Boolean visible = true; // Soft delete uchun

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}