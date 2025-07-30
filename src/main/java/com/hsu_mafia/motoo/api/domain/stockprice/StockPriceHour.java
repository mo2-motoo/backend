package com.hsu_mafia.motoo.api.domain.stockprice;

import com.hsu_mafia.motoo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "stock_price_hour")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@IdClass(StockPriceHourId.class)
public class StockPriceHour extends BaseEntity {
    
    @Id
    @Column(name = "stock_code", length = 10)
    private String stockCode;
    
    @Id
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "open_price", nullable = false, precision = 15, scale = 4)
    private BigDecimal openPrice;
    
    @Column(name = "high_price", nullable = false, precision = 15, scale = 4)
    private BigDecimal highPrice;
    
    @Column(name = "low_price", nullable = false, precision = 15, scale = 4)
    private BigDecimal lowPrice;
    
    @Column(name = "close_price", nullable = false, precision = 15, scale = 4)
    private BigDecimal closePrice;
    
    @Column(name = "volume", nullable = false)
    private Long volume;
    
    @Column(name = "amount")
    private Long amount;
    
    @Builder
    public StockPriceHour(String stockCode, LocalDateTime timestamp, 
                         BigDecimal openPrice, BigDecimal highPrice, BigDecimal lowPrice, BigDecimal closePrice,
                         Long volume, Long amount) {
        this.stockCode = stockCode;
        this.timestamp = timestamp;
        this.openPrice = openPrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.closePrice = closePrice;
        this.volume = volume;
        this.amount = amount;
    }
} 