package uz.ustazone.dto.profile.admin;

import uz.ustazone.enums.OrderStatusEnum;
import uz.ustazone.enums.PaymentMethodEnum;

import java.math.BigDecimal;
import java.time.LocalDate;

public record AdminOrderResponseDTO(
    String id,
    String title,
    String clientName,        // Mijoz ismi ("Noma'lum" bo'lishi mumkin)
    String masterName,        // Usta ismi ("Tayinlanmagan" bo'lishi mumkin)
    PaymentMethodEnum paymentMethod, // To'lov turi (CASH -> Naqd, ONLINE -> Onlayn)
    BigDecimal amount,        // Jami summa
    BigDecimal commissionAmount, // Komissiya summasi
    OrderStatusEnum status,   // Holat (PENDING, COMPLETED va h.k.)
    Boolean isEmergency,      // urgent holati uchun
    LocalDate createdDate     // Yaratilgan sana (2026-08-08)
) {}