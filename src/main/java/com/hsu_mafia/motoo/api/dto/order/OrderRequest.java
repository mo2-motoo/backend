package com.hsu_mafia.motoo.api.dto.order;

import com.hsu_mafia.motoo.api.domain.order.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequest {
    private String stockId;
    private OrderType orderType;
    private Long quantity;
    private Long price;
} 