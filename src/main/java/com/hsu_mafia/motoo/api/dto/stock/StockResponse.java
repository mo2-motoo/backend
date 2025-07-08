package com.hsu_mafia.motoo.api.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockResponse {
    private String stockId;
    private String stockName;
    private String outline;
    private Long currentPrice;
    private String marketType;
    private String industryName;
    private String priceDifference;
    private String rateDifference;
    private Long tradingVolume;
    private Integer min52;
    private Integer max52;
    private String per;
    private String pbr;
} 