package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "complaint_attaches")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ComplaintAttachEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id", nullable = false)
    private ComplaintEntity complaint;

     @Column(name = "attach_id", nullable = false)
     private String attachId;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}