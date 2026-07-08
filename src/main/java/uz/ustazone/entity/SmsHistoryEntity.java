package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.NotificationStatusEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "sms_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SmsHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "to_contact", nullable = false)
    private String toContact; // Telefon raqam

    @Column(name = "title")
    private String title; // "UstaZone"

    @Column(name = "message", columnDefinition = "text", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NotificationStatusEnum status; // SENT, FAILED

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}