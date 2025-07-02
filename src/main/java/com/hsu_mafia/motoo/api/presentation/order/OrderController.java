package com.hsu_mafia.motoo.api.presentation.order;

import com.hsu_mafia.motoo.api.dto.order.OrderRequest;
import com.hsu_mafia.motoo.api.domain.order.OrderService;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Tag(name = "Order", description = "주문 관련 API")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "주문 생성", description = "주문을 생성합니다.")
    public CommonResponse<?> placeOrder(@RequestBody OrderRequest request) {
        Long userId = 1L;
        orderService.placeOrder(userId, request);
        return CommonResponse.success(null);
    }

    @GetMapping
    @Operation(summary = "내 주문 목록 조회", description = "사용자의 주문 목록을 조회합니다.")
    public CommonResponse<?> getOrders() {
        Long userId = 1L;
        return CommonResponse.success(orderService.getOrders(userId));
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "주문 취소", description = "주문을 취소합니다.")
    public CommonResponse<?> cancelOrder(@PathVariable Long orderId) {
        Long userId = 1L;
        orderService.cancelOrder(userId, orderId);
        return CommonResponse.success(null);
    }
} 