package uz.ustazone.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.profile.admin.AdminOrderResponseDTO;
import uz.ustazone.enums.OrderStatusEnum;
import uz.ustazone.service.profile.admin.AdminOrderService;

@RestController
@RequestMapping("/api/v1/admin/orders")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Control Order API", description = "API list for admin to control orders")
public class AdminOrderController {

    @Autowired
    private AdminOrderService adminOrderService;

    @GetMapping
    @Operation(summary = "Get", description = "API used for getting master orders state")
    public ResponseEntity<Page<AdminOrderResponseDTO>> getOrders(
            @RequestParam(required = false) String search,            // Buyurtma nomi bo'yicha qidiruv
            @RequestParam(required = false) OrderStatusEnum status,   // Dropdown status filtri ("Barchasi" kelsa null)
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<AdminOrderResponseDTO> orders = adminOrderService.getOrdersForAdmin(
                search, 
                status, 
                PageRequest.of(page, size)
        );
        return ResponseEntity.ok(orders);
    }
}