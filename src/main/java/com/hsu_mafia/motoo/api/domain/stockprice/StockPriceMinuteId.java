package com.hsu_mafia.motoo.api.domain.stockprice;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StockPriceMinuteId implements Serializable {
    private String stockCode;
    private LocalDateTime timestamp;
} 