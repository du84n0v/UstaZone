package uz.ustazone.dto.profile.general;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ProfileBalanceDTO {
    private BigDecimal balance;
    private BigDecimal bonusBalance;
}