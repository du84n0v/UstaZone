package uz.ustazone.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import uz.ustazone.enums.OrderStatusEnum;

@Getter
@Setter
public class OrderAdvanceDTO {
    @NotNull(message = "Yangi status kiritilishi shart")
    private OrderStatusEnum status;
}