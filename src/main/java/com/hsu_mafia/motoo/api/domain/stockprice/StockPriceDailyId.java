package com.hsu_mafia.motoo.api.domain.stockprice;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class StockPriceDailyId implements Serializable {
    private String stockCode;
    private LocalDate date;
} 