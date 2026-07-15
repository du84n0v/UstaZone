package uz.ustazone.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uz.ustazone.dto.order.OrderShortResponseDTO;
import uz.ustazone.service.OrderService;
import uz.ustazone.util.PageUtil;

import java.util.List;

@RestController
@RequestMapping("/api/v1/public/orders")
public class PublicOrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping
    public ResponseEntity<Page<OrderShortResponseDTO>> getPublicOrders(
            @RequestParam(required = false) List<String> categoryId,
            @RequestParam(required = false) String region,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Page<OrderShortResponseDTO> orders = orderService.getPublicOrders(categoryId, region, PageUtil.page(page), size);
        return ResponseEntity.ok(orders);
    }
}