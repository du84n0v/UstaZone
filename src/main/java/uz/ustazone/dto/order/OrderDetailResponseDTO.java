package uz.ustazone.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import uz.ustazone.dto.category.CategoryShortInfoDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class OrderDetailResponseDTO {
    private String id;
    private String title;
    private String description;
    private String city;
    private String address;
    private String status;
    private String paymentMethod;
    
    private BigDecimal amount;
    private BigDecimal discount;
    private BigDecimal commissionAmount;
    private BigDecimal masterAmount;
    
    private Boolean isEmergency;
    private Boolean isDispute;
    
    // Lokatsiya koordinatalari
    private Double clientLat;
    private Double clientLng;
    private Double masterLat;
    private Double masterLng;
    
    // Foydalanuvchilar haqida ma'lumot
    private String clientFullName;
    private String clientPhone; 
    
    private String masterFullName;
    private String masterPhone;
    
    private List<CategoryShortInfoDTO> categories;
    
    // Muddatlar
    private LocalDateTime createdDate;
    private LocalDateTime acceptedDate;
    private LocalDateTime startedDate;
    private LocalDateTime completedDate;
    private LocalDateTime cancelledDate;
}