package com.hsu_mafia.motoo.api.presentation.order;

import com.hsu_mafia.motoo.api.domain.order.OrderService;
import com.hsu_mafia.motoo.api.dto.order.OrderRequest;
import com.hsu_mafia.motoo.api.dto.order.OrderResponse;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public CommonResponse<Void> placeOrder(@RequestBody OrderRequest request) {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경
        orderService.placeOrder(userId, request);
        return CommonResponse.success();
    }

    @GetMapping
    public CommonResponse<List<OrderResponse>> getOrders() {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경
        List<OrderResponse> orders = orderService.getOrders(userId);
        return CommonResponse.success(orders);
    }

    @DeleteMapping("/{orderId}")
    public CommonResponse<Void> cancelOrder(@PathVariable Long orderId) {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경
        orderService.cancelOrder(userId, orderId);
        return CommonResponse.success();
    }
} 