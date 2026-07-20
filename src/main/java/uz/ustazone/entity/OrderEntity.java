package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import uz.ustazone.enums.OrderStatusEnum;
import uz.ustazone.enums.PaymentMethodEnum;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // --- ROLE_CLIENT BOG'LANISHI ---
    @Column(name = "client_id", nullable = false)
    private String clientId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", insertable = false, updatable = false)
    private ProfileEntity client;

    // --- MASTER BOG'LANISHI (NULLABLE) ---
    @Column(name = "master_id")
    private String masterId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_id", insertable = false, updatable = false)
    private ProfileEntity master;

    // --- CATEGORY BOG'LANISHI ---
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<OrderCategoryEntity> categories = new ArrayList<>();

    // --- PROMO CODE BOG'LANISHI ---
    @Column(name = "promo_code_id")
    private String promoCodeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promo_code_id", insertable = false, updatable = false)
    private PromoCodeEntity promoCode;

    // --- UMUMIY MA'LUMOTLAR ---
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    @Column(name = "city")
    private String city;

    @Column(name = "address")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method")
    @Builder.Default
    private PaymentMethodEnum paymentMethod = PaymentMethodEnum.CASH;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private OrderStatusEnum status = OrderStatusEnum.PENDING;

    // --- MOLIYAVIY MAYDONLAR ---
    @Column(name = "amount")
    @Builder.Default
    private BigDecimal amount = BigDecimal.ZERO;

    @Column(name = "discount")
    @Builder.Default
    private BigDecimal discount = BigDecimal.ZERO;

    @Column(name = "bonus_used")
    @Builder.Default
    private BigDecimal bonusUsed = BigDecimal.ZERO;

    @Column(name = "commission_amount")
    @Builder.Default
    private BigDecimal commissionAmount = BigDecimal.ZERO;

    @Column(name = "master_amount")
    @Builder.Default
    private BigDecimal masterAmount = BigDecimal.ZERO;

    // --- TASDIQLAR ---
    @Column(name = "client_confirmed")
    @Builder.Default
    private Boolean clientConfirmed = false;

    @Column(name = "master_confirmed")
    @Builder.Default
    private Boolean masterConfirmed = false;

    @Column(name = "admin_approved")
    @Builder.Default
    private Boolean adminApproved = false;

    @Column(name = "is_dispute")
    @Builder.Default
    private Boolean isDispute = false;

    @Column(name = "is_emergency")
    @Builder.Default
    private Boolean isEmergency = false;

    // --- GEOLOKATSIYALAR ---
    @Column(name = "client_lat")
    private Double clientLat;

    @Column(name = "client_lng")
    private Double clientLng;

    @Column(name = "master_lat")
    private Double masterLat;

    @Column(name = "master_lng")
    private Double masterLng;

    @Column(name = "cancel_reason")
    private String cancelReason;

    // --- TIMESTAMPS ---
    @Column(name = "accepted_date")
    private LocalDateTime acceptedDate;

    @Column(name = "enroute_date")
    private LocalDateTime enrouteDate;

    @Column(name = "started_date")
    private LocalDateTime startedDate;

    @Column(name = "completed_date")
    private LocalDateTime completedDate;

    @Column(name = "cancelled_date")
    private LocalDateTime cancelledDate;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;
}