package uz.ustazone.dto.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import uz.ustazone.enums.PaymentMethodEnum;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class OrderCreateDTO {
    @NotEmpty(message = "Kategoriya tanlanishi shart")
    private List<String> categoryIds;

    @NotBlank(message = "Buyurtma sarlavhasi bo'sh bo'lmasligi kerak")
    private String title;

    private String description;

    @NotBlank(message = "Shahar kiritilishi shart")
    private String city;

    @NotBlank(message = "Aniq manzil kiritilishi shart")
    private String address;

    @NotNull(message = "Lokatsiya koordinatasi (Lat) yetishmayapti")
    private Double clientLat;

    @NotNull(message = "Lokatsiya koordinatasi (Lng) yetishmayapti")
    private Double clientLng;

    @NotNull(message = "To'lov turi tanlanishi shart")
    private PaymentMethodEnum paymentMethod;

    @NotNull(message = "Xizmat haqi kiritilishi shart")
    @Positive(message = "Narx 0 dan katta bo'lishi kerak")
    private BigDecimal amount;

    private String promoCodeId; // Nullable
    private Boolean isEmergency = false; // Default false
}