package uz.ustazone.controller.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import uz.ustazone.dto.order.OrderCreateDTO;
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
        Page<OrderShortResponseDTO> orders = orderService.getPublicOrders(categoryIds, region, PageUtil.page(page), size);
        return ResponseEntity.ok(orders);
    }

    @PreAuthorize("hasRole('MASTER')")
    @PostMapping("/{id}/claim")
    @io.swagger.v3.oas.annotations.Operation(summary = "Claim Order", description = "Ustalar tomonidan buyurtmani olish (qabul qilish)")
    public org.springframework.http.ResponseEntity<String> claimOrder(@PathVariable String id) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return org.springframework.http.ResponseEntity.ok(orderService.claimOrder(masterId, id));
    }

    @PreAuthorize("hasAnyRole('MASTER', 'CLIENT', 'ADMIN')")
    @PatchMapping("/{id}/advance")
    @Operation(summary = "Advance Status", description = "Buyurtma holatini keyingi bosqichga o'tkazish (Masalan: ACCEPTED -> EN_ROUTE)")
    public org.springframework.http.ResponseEntity<String> advanceOrderStatus(
            @PathVariable String id,
            @jakarta.validation.Valid @RequestBody uz.ustazone.dto.order.OrderAdvanceDTO dto) {

        String profileId = SpringSecurityUtil.getCurrentProfileId();
        return org.springframework.http.ResponseEntity.ok(orderService.advanceOrderStatus(profileId, id, dto.getStatus()));
    }

    @org.springframework.web.bind.annotation.GetMapping("/{id}")
    @io.swagger.v3.oas.annotations.Operation(summary = "Get Detail", description = "Buyurtmaning to'liq ma'lumotlarini (Detail) olish")
    public org.springframework.http.ResponseEntity<uz.ustazone.dto.order.OrderDetailResponseDTO> getOrderById(@org.springframework.web.bind.annotation.PathVariable String id) {
        return org.springframework.http.ResponseEntity.ok(orderService.getOrderDetail(id));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('CLIENT')")
    @org.springframework.web.bind.annotation.GetMapping("/client")
    @io.swagger.v3.oas.annotations.Operation(summary = "Client Orders", description = "Mijozning o'z buyurtmalari ro'yxati")
    public org.springframework.http.ResponseEntity<org.springframework.data.domain.Page<OrderShortResponseDTO>> getMyClientOrders(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size) {
        String clientId = SpringSecurityUtil.getCurrentProfileId();
        return org.springframework.http.ResponseEntity.ok(orderService.getClientOrders(clientId, page, size));
    }

    @org.springframework.security.access.prepost.PreAuthorize("hasRole('MASTER')")
    @org.springframework.web.bind.annotation.GetMapping("/master")
    @io.swagger.v3.oas.annotations.Operation(summary = "Master Orders", description = "Ustaning o'zi qabul qilgan buyurtmalari ro'yxati")
    public org.springframework.http.ResponseEntity<org.springframework.data.domain.Page<OrderShortResponseDTO>> getMyMasterOrders(
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page,
            @org.springframework.web.bind.annotation.RequestParam(defaultValue = "10") int size) {
        String masterId = SpringSecurityUtil.getCurrentProfileId();
        return org.springframework.http.ResponseEntity.ok(orderService.getMasterOrders(masterId, page, size));
    }
}