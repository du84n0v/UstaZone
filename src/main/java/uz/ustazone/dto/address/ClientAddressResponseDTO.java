package uz.ustazone.dto.address;

import lombok.*;
import uz.ustazone.enums.AddressTypeEnum;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ClientAddressResponseDTO {
    private String id;
    private String region;
    private String addressLine;
    private AddressTypeEnum type;
    private LocalDateTime createdDate;
}
