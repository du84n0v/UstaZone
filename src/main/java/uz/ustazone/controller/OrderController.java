package uz.ustazone.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uz.ustazone.dto.order.OrderCreateDTO;
import uz.ustazone.service.OrderService;
import uz.ustazone.util.SpringSecurityUtil;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<String> createOrder(@Valid @RequestBody OrderCreateDTO dto) {
        String clientId = SpringSecurityUtil.getCurrentProfileId();
        return ResponseEntity.ok(orderService.createOrder(clientId, dto));
    }
}