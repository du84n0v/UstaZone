package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}