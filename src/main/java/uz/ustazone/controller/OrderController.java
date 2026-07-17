package uz.ustazone.controller;

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
import uz.ustazone.service.OrderService;
import uz.ustazone.util.PageUtil;
import uz.ustazone.util.SpringSecurityUtil;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Order API", description = "API list for orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PreAuthorize("hasAnyAuthority('MASTER', 'CLIENT')")
    @PostMapping
    @Operation(summary = "Create", description = "API used for Create Order")
    public ResponseEntity<String> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        String clientId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(orderService.createOrder(clientId, dto));
    }

    @GetMapping
    @Operation(summary = "Get", description = "API used for getting pending orders for everyone")
    public ResponseEntity<Page<OrderShortResponseDTO>> getPublicOrders(
            @RequestParam(required = false) List<String> categoryIds,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderShortResponseDTO> orders = orderService.getPublicOrders(categoryIds, region, PageUtil.page(page), size);
        return ResponseEntity.ok(orders);
    }
}