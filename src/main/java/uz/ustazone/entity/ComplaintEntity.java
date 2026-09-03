package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.ComplaintCategoryEnum;
import uz.ustazone.enums.ComplaintStatusEnum;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // Shikoyat kategoriyasi (tegi: FIRIBGAR, SIFATSIZ...)
    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private ComplaintCategoryEnum category;

    // Shikoyat matni ("Bu odam firibgarlik qildi")
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    // Kim yozdi (Mijoz yoki Usta)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporter_id", nullable = false)
    private ProfileEntity reporter;

    // Kimning ustidan shikoyat qilindi (Usta yoki Mijoz)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_id", nullable = false)
    private ProfileEntity target;

    // Qaysi buyurtma ustida nizo bo'lgan (ixtiyoriy)
    @Column(name = "order_id")
    private String orderId;

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ComplaintAttachEntity> attaches = new ArrayList<>();

    // Status: OPEN, RESOLVED, REJECTED
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ComplaintStatusEnum status = ComplaintStatusEnum.OPEN;

    // Admin kiritadigan izoh (Skrinshotdagi textarea)
    @Column(name = "admin_note", columnDefinition = "TEXT")
    private String adminNote;

    @Column(name = "resolved_date")
    private LocalDateTime resolvedDate;

    @Column(name = "resolved_by_admin_id")
    private String resolvedByAdminId;

    @Column(name = "visible", nullable = false)
    @Builder.Default
    private Boolean visible = true;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}