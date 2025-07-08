package com.hsu_mafia.motoo.api.dto.order;

import com.hsu_mafia.motoo.api.domain.order.OrderStatus;
import com.hsu_mafia.motoo.api.domain.order.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {
    private Long orderId;
    private String stockId;
    private String stockName;
    private OrderType orderType;
    private Long quantity;
    private Long price;
    private OrderStatus status;
    private LocalDateTime createdAt;
} 