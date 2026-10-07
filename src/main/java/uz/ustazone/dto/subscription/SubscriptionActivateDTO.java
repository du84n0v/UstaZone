package uz.ustazone.dto.subscription;

import lombok.Getter;
import lombok.Setter;
import uz.ustazone.enums.TariffTierEnum;
import uz.ustazone.enums.BillingPeriodEnum;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
public class SubscriptionActivateDTO {
    @NotNull(message = "Tarif (tier) kiritilishi shart")
    private TariffTierEnum tier;
    
    @NotNull(message = "Muddat (billingPeriod) kiritilishi shart")
    private BillingPeriodEnum billingPeriod; // MONTHLY yoki YEARLY
}