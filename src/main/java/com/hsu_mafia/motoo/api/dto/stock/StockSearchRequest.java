package com.hsu_mafia.motoo.api.dto.stock;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockSearchRequest {
    private String keyword;
    private String marketType;
    private String industryName;
} 