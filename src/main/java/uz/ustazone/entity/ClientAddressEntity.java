package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import uz.ustazone.enums.AddressTypeEnum;

import java.time.LocalDateTime;

@Entity
@Table(name = "client_address")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientAddressEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "client_id", nullable = false)
    private String clientId; // Qaysi mijozga tegishli ekanligi

    @Enumerated(EnumType.STRING)
    @Column(name = "address_type", nullable = false)
    private AddressTypeEnum addressType; // UY (HOME), ISH (WORK), BOSHQA (OTHER)

    @Column(name = "region", nullable = false)
    private String region;

    @Column(name = "address_line", nullable = false)
    private String addressLine; // "ko'cha, uy, kvartira" yoziladigan joy

    @Column(name = "visible")
    private Boolean visible = true;

    // Xaritadan aniq usta qidirish uchun koordinatalar ham saqlanishi shart
    private Double latitude;
    private Double longitude;

    @Column(name = "createdDate", nullable = false)
    @CreationTimestamp
    private LocalDateTime createdDate;
}