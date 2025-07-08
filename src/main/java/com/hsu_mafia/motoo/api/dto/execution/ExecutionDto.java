package com.hsu_mafia.motoo.api.dto.execution;

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
public class ExecutionDto {
    private Long executionId;
    private String stockId;
    private String stockName;
    private OrderType orderType;
    private Long quantity;
    private Long executedPrice;
    private LocalDateTime executedAt;
} 