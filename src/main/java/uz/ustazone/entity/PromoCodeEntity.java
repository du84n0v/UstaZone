package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import uz.ustazone.enums.PromoCodeTypeEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "promo_codes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromoCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "code", nullable = false, unique = true)
    private String code; // WELCOME10 (Katta harflarda)

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private PromoCodeTypeEnum type; // PERCENT yoki FIXED_AMOUNT

    @Column(name = "discount_value", nullable = false)
    private Double discountValue; // 10 (%) yoki 100000 (so'm)

    @Column(name = "max_usage_count")
    private Integer maxUsageCount; // Bo'sh bo'lsa (null) = cheksiz

    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private Integer usedCount = 0; // Ishlatilganlar soni (1/1)

    @Column(name = "min_order_amount", nullable = false)
    @Builder.Default
    private Long minOrderAmount = 0L; // Min. order summasi (so'm)

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true; // Faol (yashil) / Yopiq (kulrang)

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