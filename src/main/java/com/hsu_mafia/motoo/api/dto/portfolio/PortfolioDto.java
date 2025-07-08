package com.hsu_mafia.motoo.api.dto.portfolio;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PortfolioDto {
    private Long userId;
    private String username;
    private Long seedMoney;
    private Long cash;
    private Long totalStockValue;
    private Long totalValue;
    private Long netProfit;
    private List<PortfolioStockDto> stocks;
} 