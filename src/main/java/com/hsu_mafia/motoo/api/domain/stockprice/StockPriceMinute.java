package com.hsu_mafia.motoo.api.domain.stockprice;

import com.hsu_mafia.motoo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_price_minute")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@IdClass(StockPriceMinuteId.class)
public class StockPriceMinute extends BaseEntity {
    
    @Id
    @Column(name = "stock_code", length = 10)
    private String stockCode;
    
    @Id
    @Column(name = "timestamp")
    private LocalDateTime timestamp;
    
    @Column(name = "open_price", nullable = false)
    private Long openPrice;
    
    @Column(name = "high_price", nullable = false)
    private Long highPrice;
    
    @Column(name = "low_price", nullable = false)
    private Long lowPrice;
    
    @Column(name = "close_price", nullable = false)
    private Long closePrice;
    
    @Column(name = "volume", nullable = false)
    private Long volume;
    
    @Column(name = "amount")
    private Long amount;
    
    @Builder
    public StockPriceMinute(String stockCode, LocalDateTime timestamp, 
                           Long openPrice, Long highPrice, Long lowPrice, Long closePrice,
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