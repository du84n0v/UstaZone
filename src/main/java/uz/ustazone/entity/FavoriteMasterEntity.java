package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "favorite_master")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FavoriteMasterEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

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

    @CreationTimestamp
    @Column(name = "created_date", updatable = false)
    private LocalDateTime createdDate;
}