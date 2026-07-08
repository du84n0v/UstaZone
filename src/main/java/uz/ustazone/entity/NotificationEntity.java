package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.NotificationTypeEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notification")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // --- USER (KIMGA) ---
    @Column(name = "user_id", nullable = false)
    private String userId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private ProfileEntity user;

    // --- SENDER (KIMDAN - NULLABLE) ---
    @Column(name = "sender_id")
    private String senderId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    private ProfileEntity sender;

    // --- RELATED ORDER (NULLABLE) ---
    @Column(name = "related_order_id")
    private String relatedOrderId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_order_id", insertable = false, updatable = false)
    private OrderEntity relatedOrder;

    // --- MA'LUMOTLAR ---
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NotificationTypeEnum type;

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    // --- CLAIM LOGIC (BUYURTMANI ILIB KETISH) ---
    @Column(name = "claimed_by")
    private String claimedBy;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "claimed_by", insertable = false, updatable = false)
    private ProfileEntity claimedUser;

    @Column(name = "claimed_date")
    private LocalDateTime claimedDate;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}