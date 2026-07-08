package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "platform_settings")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlatformSettingsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "commission_percent", nullable = false)
    @Builder.Default
    private BigDecimal commissionPercent = BigDecimal.ZERO; // Masalan: 10.0%

    @Column(name = "config_key", nullable = false, unique = true) // 'key' SQL rezerv so'zi bo'lishi mumkin, config_key xavfsizroq
    private String key;

    @Column(name = "config_value", nullable = false)
    private String value;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}