package com.hsu_mafia.motoo.api.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class StockResponse {
    private String id;
    private String stockName;
    private String outline;
    private Long currentPrice;
    private String marketType;
    private String industryName;
} 