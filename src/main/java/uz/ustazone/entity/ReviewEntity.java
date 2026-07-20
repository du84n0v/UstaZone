package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "review")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // --- ORDER BOG'LANISHI (UNIQUE) ---
    @Column(name = "order_id", nullable = false, unique = true)
    private String orderId;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private OrderEntity order;

    // --- ROLE_CLIENT BOG'LANISHI ---
    @Column(name = "client_id", nullable = false)
    private String clientId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private ProfileEntity client;

    // --- MASTER BOG'LANISHI ---
    @Column(name = "master_id", nullable = false)
    private String masterId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", insertable = false, updatable = false)
    private ProfileEntity master;

    // --- REYTING VA SHARH ---
    @Column(name = "rating", nullable = false)
    private Integer rating; // 1 dan 5 gacha

    @Column(name = "comment", columnDefinition = "text")
    private String comment;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}