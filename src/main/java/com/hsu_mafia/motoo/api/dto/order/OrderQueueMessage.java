package com.hsu_mafia.motoo.api.dto.order;

import com.hsu_mafia.motoo.api.domain.order.OrderType;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderQueueMessage {
    private Long orderId;
    private Long userId;
    private String stockCode;
    private OrderType orderType;
    private Long quantity;
    private Long price; // 지정가 (시장가인 경우 null)
    private LocalDateTime createdAt;
    private boolean isMarketOrder; // 시장가 주문 여부
    
    public static OrderQueueMessage fromOrder(Long orderId, Long userId, String stockCode, 
                                            OrderType orderType, Long quantity, Long price, 
                                            LocalDateTime createdAt, boolean isMarketOrder) {
        return OrderQueueMessage.builder()
                .orderId(orderId)
                .userId(userId)
                .stockCode(stockCode)
                .orderType(orderType)
                .quantity(quantity)
                .price(price)
                .createdAt(createdAt)
                .isMarketOrder(isMarketOrder)
                .build();
    }
} 