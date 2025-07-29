package com.hsu_mafia.motoo.api.presentation.order;

import com.hsu_mafia.motoo.api.domain.order.Order;
import com.hsu_mafia.motoo.api.domain.order.OrderMapper;
import com.hsu_mafia.motoo.api.domain.order.OrderService;
import com.hsu_mafia.motoo.api.dto.order.OrderRequest;
import com.hsu_mafia.motoo.api.dto.order.OrderResponse;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;
    private final OrderMapper orderMapper;

    @PostMapping
    @Operation(summary = "주문 생성", description = "주식 매수/매도 주문을 생성합니다. (시장가/지정가 모두 지원)")
    public ResponseEntity<CommonResponse<Void>> placeOrder(@RequestBody OrderRequest request) {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경
        orderService.placeOrder(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }

    @GetMapping
    @Operation(summary = "주문 내역 조회", description = "사용자의 전체 주문 내역을 조회합니다.")
    public ResponseEntity<CommonResponse<List<OrderResponse>>> getOrders(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경

        Pageable pageable = PageRequest.of(page, size);

        List<Order> orders = orderService.getOrders(userId, pageable);
        List<OrderResponse> orderResponses = orders.stream()
                .map(orderMapper::toOrderResponse)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(orderResponses));
    }

    @DeleteMapping("/{orderId}")
    @Operation(summary = "주문 취소", description = "특정 주문을 취소합니다. (PENDING 상태만 가능)")
    public ResponseEntity<CommonResponse<Void>> cancelOrder(@PathVariable Long orderId) {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경

        orderService.cancelOrder(userId, orderId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT)
                .body(CommonResponse.success());
    }
} 
