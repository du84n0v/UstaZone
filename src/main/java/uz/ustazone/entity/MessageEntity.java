package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "message")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // --- SENDER (YUBORUVCHI) ---
    @Column(name = "sender_id", nullable = false)
    private String senderId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", insertable = false, updatable = false)
    private ProfileEntity sender;

    // --- RECEIVER (QABUL QILUVCHI) ---
    @Column(name = "receiver_id", nullable = false)
    private String receiverId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", insertable = false, updatable = false)
    private ProfileEntity receiver;

    // --- ORDER (BUYURTMA - NULLABLE) ---
    @Column(name = "order_id")
    private String orderId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private OrderEntity order;

    // --- KONTENT VA FAYL ---
    @Column(name = "content", columnDefinition = "text")
    private String content;

    @Column(name = "attachment_id")
    private String attachmentId;

    // --- STATUS VA VAQT ---
    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}