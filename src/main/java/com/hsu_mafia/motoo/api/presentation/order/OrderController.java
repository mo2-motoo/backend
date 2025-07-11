package com.hsu_mafia.motoo.api.presentation.order;

import com.hsu_mafia.motoo.api.domain.order.OrderService;
import com.hsu_mafia.motoo.api.dto.order.OrderRequest;
import com.hsu_mafia.motoo.api.dto.order.OrderResponse;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    @Operation(summary = "주문 생성", description = "주식 매수/매도 주문을 생성합니다. (시장가/지정가 모두 지원)")
    public CommonResponse<Void> placeOrder(@RequestBody OrderRequest request) {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경
        orderService.placeOrder(userId, request);
        return CommonResponse.success();
    }

    @GetMapping
    @Operation(summary = "주문 내역 조회", description = "사용자의 전체 주문 내역을 조회합니다.")
    public CommonResponse<List<OrderResponse>> getOrders() {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경
        List<OrderResponse> orders = orderService.getOrders(userId);
        return CommonResponse.success(orders);
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "주문 취소", description = "특정 주문을 취소합니다. (PENDING 상태만 가능)")
    public CommonResponse<Void> cancelOrder(@PathVariable Long orderId) {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경
        orderService.cancelOrder(userId, orderId);
        return CommonResponse.success();
    }
} 
