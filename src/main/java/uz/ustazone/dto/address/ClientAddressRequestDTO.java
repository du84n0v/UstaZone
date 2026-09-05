package uz.ustazone.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.ustazone.enums.AddressTypeEnum;

@Getter
@Setter
public class ClientAddressRequestDTO {
    @NotNull(message = "Manzil turi tanlanishi shart")
    private AddressTypeEnum type;

    @NotBlank(message = "Manzil kiritilishi shart")
    private String address;

    @NotBlank(message = "Viloyat kiritilishi shart")
    private String region;
}