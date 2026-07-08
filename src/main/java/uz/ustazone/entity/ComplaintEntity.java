package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.ComplaintStatusEnum;
import uz.ustazone.enums.ComplaintTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "complaint")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComplaintEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // --- SHIKOYAT QILUVCHI ---
    @Column(name = "profile_id", nullable = false)
    private String profileId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id", insertable = false, updatable = false)
    private ProfileEntity profile;

    // --- SHIKOYAT MATNI ---
    @Column(name = "content", columnDefinition = "text", nullable = false)
    private String content;

    // --- DINAMIK BOG'LANISH (POLYMORPHIC) ---
    @Column(name = "entity_id", nullable = false)
    private String entityId; // Dinamik ravishda order_id yoki profile_id bo'lishi mumkin

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private ComplaintTypeEnum type; // ORDER, MASTER, CLIENT

    // --- HOLAT VA VAQT ---
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private ComplaintStatusEnum status = ComplaintStatusEnum.OPEN;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}