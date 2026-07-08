package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.BroadcastAudienceEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "broadcast")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BroadcastEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    // --- ADMIN BOG'LANISHI ---
    @Column(name = "admin_id", nullable = false)
    private String adminId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id", insertable = false, updatable = false)
    private ProfileEntity admin;

    // --- AUDITORIYA VA KONTENT ---
    @Enumerated(EnumType.STRING)
    @Column(name = "audience", nullable = false)
    private BroadcastAudienceEnum audience; // ALL, CLIENTS, MASTERS

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "message", columnDefinition = "text", nullable = false)
    private String message;

    // --- KANAL VA VAQT ---
    @Column(name = "send_sms", nullable = false)
    @Builder.Default
    private Boolean sendSms = false;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}