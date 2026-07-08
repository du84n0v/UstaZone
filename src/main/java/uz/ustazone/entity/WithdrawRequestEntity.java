package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.WithdrawProviderEnum;
import uz.ustazone.enums.WithdrawStatusEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "withdraw_request")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawRequestEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // --- MASTER BOG'LANISHI ---
    @Column(name = "master_id", nullable = false)
    private String masterId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", insertable = false, updatable = false)
    private ProfileEntity master;

    // --- MOLIYAVIY DETALLAR ---
    @Column(name = "amount", nullable = false)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private WithdrawStatusEnum status = WithdrawStatusEnum.PENDING; // PENDING, APPROVED, REJECTED, PAID

    @Enumerated(EnumType.STRING)
    @Column(name = "provider", nullable = false)
    private WithdrawProviderEnum provider; // CLICK, PAYME, BANK

    @Column(name = "provider_ref")
    private String providerRef;

    // --- VAQT KO'RSATKICHLARI ---
    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;
}