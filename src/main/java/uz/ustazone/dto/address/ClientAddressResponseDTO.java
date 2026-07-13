package uz.ustazone.dto.address;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uz.ustazone.enums.AddressTypeEnum;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ClientAddressResponseDTO {
    private String region;
    private String city;
    private String addressLine;
    private AddressTypeEnum type;
}
