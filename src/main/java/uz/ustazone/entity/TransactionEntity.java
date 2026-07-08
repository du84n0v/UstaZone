package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.PaymentProviderEnum;
import uz.ustazone.enums.TransactionStatusEnum;
import uz.ustazone.enums.TransactionTypeEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "transaction")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // --- USER BOG'LANISHI ---
    @Column(name = "user_id", nullable = false)
    private String userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private ProfileEntity user;

    // --- ORDER BOG'LANISHI (NULLABLE) ---
    @Column(name = "order_id")
    private String orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", insertable = false, updatable = false)
    private OrderEntity order;

    // --- MOLIYAVIY DETALLAR ---
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private TransactionTypeEnum type;

    @Column(name = "amount", nullable = false)
    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private TransactionStatusEnum status = TransactionStatusEnum.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private PaymentProviderEnum provider;

    @Column(name = "provider_ref")
    private String providerRef; // Click/Payme yuboradigan trans_id

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}