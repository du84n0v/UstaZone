package uz.ustazone.controller.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.order.OrderAdvanceDTO;
import uz.ustazone.dto.order.OrderCreateDTO;
import uz.ustazone.dto.order.OrderDetailResponseDTO;
import uz.ustazone.dto.order.OrderShortResponseDTO;
import uz.ustazone.service.order.OrderService;
import uz.ustazone.util.PageUtil;
import uz.ustazone.util.SpringSecurityUtil;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order API", description = "API list for orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAnyRole('MASTER', 'CLIENT')")
    @PostMapping
    @Operation(summary = "Create", description = "API used for Create Order")
    public ResponseEntity<String> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        String clientId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(orderService.createOrder(clientId, dto));
    }

    @GetMapping
    @Operation(summary = "Get", description = "API used for getting pending orders for everyone by category or region or nothing")
    public ResponseEntity<Page<OrderShortResponseDTO>> getPublicOrders(
            @RequestParam(required = false) List<String> categoryIds,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderShortResponseDTO> orders = orderService.getPublicOrders(categoryIds, region, page, size);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/{id}/claim")
    @Operation(summary = "Claim Order", description = "Ustalar tomonidan buyurtmani olish (qabul qilish)")
    public ResponseEntity<String> claimOrder(@PathVariable String id) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(orderService.claimOrder(masterId, id));
    }

    @PreAuthorize("hasAnyRole('MASTER', 'CLIENT', 'ADMIN')")
    @PatchMapping("/{id}/advance")
    @Operation(summary = "Advance Status", description = "Buyurtma holatini keyingi bosqichga o'tkazish (Masalan: ACCEPTED -> EN_ROUTE)")
    public ResponseEntity<String> advanceOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody OrderAdvanceDTO dto) {

        String profileId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(orderService.advanceOrderStatus(profileId, id, dto.getStatus()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get Detail", description = "Buyurtmaning to'liq ma'lumotlarini (Detail) olish")
    public ResponseEntity<OrderDetailResponseDTO> getOrderById(@PathVariable String id) {
        return ResponseEntity.ok(orderService.getOrderDetail(id));
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/client")
    @Operation(summary = "Client Orders", description = "Mijozning o'z buyurtmalari ro'yxati")
    public ResponseEntity<Page<OrderShortResponseDTO>> getMyClientOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String clientId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(orderService.getClientOrders(clientId, page, size));
    }

    @PreAuthorize("hasRole('MASTER')")
    @GetMapping("/master")
    @Operation(summary = "Master Orders", description = "Ustaning o'zi qabul qilgan buyurtmalari ro'yxati")
    public ResponseEntity<Page<OrderShortResponseDTO>> getMyMasterOrders(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(orderService.getMasterOrders(masterId, page, size));
    }
}