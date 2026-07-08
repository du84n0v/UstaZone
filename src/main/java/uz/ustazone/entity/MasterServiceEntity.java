package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.PriceTypeEnum;
import uz.ustazone.enums.StatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "master_service")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterServiceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "master_id")
    private String masterProfileId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", nullable = false, insertable = false, updatable = false)
    private MasterProfileEntity masterProfile;

    @Column(name = "subcategory_id")
    private String subCategoryId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id", nullable = false, insertable = false, updatable = false)
    private CategoryEntity subCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_type", nullable = false)
    private PriceTypeEnum priceType;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "is_negotiable")
    private Boolean isNegotiable = false;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusEnum status = StatusEnum.ACTIVE;

    @OneToMany(mappedBy = "masterService", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<MasterServicePortfolioEntity> portfolioImages = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}