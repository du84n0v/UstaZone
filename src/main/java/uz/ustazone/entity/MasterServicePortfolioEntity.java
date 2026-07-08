package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "master_service_portfolio")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MasterServicePortfolioEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "master_service_id")
    private String masterServiceId;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "master_service_id", nullable = false, insertable = false, updatable = false)
    private MasterServiceEntity masterService;

    @Column(name = "attach_id", nullable = false)
    private String attachId;

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}