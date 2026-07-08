package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "promo_code")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromoCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "discount_percent")
    private Integer discountPercent; // Masalan: 10, 15 (foizda)

    @Column(name = "discount_amount")
    private BigDecimal discountAmount; // Masalan: 20000.00 (so'mda)

    @Column(name = "max_usage", nullable = false)
    @Builder.Default
    private Integer maxUsage = 0;

    @Column(name = "used_count", nullable = false)
    @Builder.Default
    private Integer usedCount = 0;

    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    @Column(name = "valid_to", nullable = false)
    private LocalDateTime validTo;

    @Column(name = "is_active", nullable = false)
    @Builder.Default
    private Boolean isActive = true;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}