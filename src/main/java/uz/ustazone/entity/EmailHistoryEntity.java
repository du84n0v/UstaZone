package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.NotificationStatusEnum;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "email_history")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailHistoryEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "to_contact", nullable = false)
    private String toContact; // Email manzil

    @Column(name = "title", nullable = false)
    private String title; // Email Subject

    @Column(name = "message", columnDefinition = "text", nullable = false)
    private String message;

    @Column(name = "code", nullable = false)
    private String code;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}