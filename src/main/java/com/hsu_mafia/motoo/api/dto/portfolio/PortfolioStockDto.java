package com.hsu_mafia.motoo.api.dto.portfolio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioStockDto {
    private String stockId;
    private String stockName;
    private Long quantity;
    private Long averageBuyPrice;
    private Long currentPrice;
    private Long currentValue;
    private Long profitLoss;
    private Double profitLossRate;
} 