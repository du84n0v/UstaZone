package uz.ustazone.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import uz.ustazone.enums.AddressTypeEnum;

@Entity
@Table(name = "client_address")
@Getter
@Setter
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

    @Column(name = "city", nullable = false)
    private String city;

    @Column(name = "address_line", nullable = false)
    private String addressLine; // "ko'cha, uy, kvartira" yoziladigan joy

    // Xaritadan aniq usta qidirish uchun koordinatalar ham saqlanishi shart
    private Double latitude;
    private Double longitude;
}