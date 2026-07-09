package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "verification_attempt")
public class VerificationAttemptEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column
    private String email;

    @Column(name = "attempt_count")
    private Integer attemptCount;

    @Column(name = "last_attempt")
    private LocalDateTime lastAttempt;

    @Column(name = "resend_count")
    private Integer resendCount;

    @Column(name = "last_resend_time")
    private LocalDateTime lastResendTime;
}