package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.PriceTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "master_category")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterCategoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "master_profile_id")
    private String masterProfileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_profile_id", nullable = false, insertable = false, updatable = false)
    private MasterProfileEntity masterProfile;

    @Column(name = "catedory_id")
    private String categoryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, insertable = false, updatable = false)
    private CategoryEntity category;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_type", nullable = false)
    private PriceTypeEnum priceType = PriceTypeEnum.FROM; // Default: Boshlab

    @Column(name = "min_price")
    private BigDecimal minPrice; // Boshlang'ich yoki aniq narx shu yerga yoziladi

    @Column(name = "max_price")
    private BigDecimal maxPrice;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}