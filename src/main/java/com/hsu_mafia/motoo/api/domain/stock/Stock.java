package com.hsu_mafia.motoo.api.domain.stock;

import com.hsu_mafia.motoo.api.domain.stock.Industry;
import com.hsu_mafia.motoo.global.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "stocks")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Stock extends BaseEntity {
    @Id
    @Column(name = "stock_code", length = 10)
    private String stockCode; // 종목 코드 (ex. 005930)

    @Column(name = "stock_name", nullable = false, length = 50)
    private String stockName;

    @Column(length = 255)
    private String outline;

    @Column(name = "market_type", length = 20)
    private String marketType; // KOSPI, NASDAQ 등

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true; // 데이터 수집 활성화 여부

    @Column(name = "ranking")
    private Integer ranking; // 시장 내 순위

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Industry industry;
    
    @Builder
    public Stock(String stockCode, String stockName, String outline, 
                String marketType, Industry industry, Integer ranking) {
        this.stockCode = stockCode;
        this.stockName = stockName;
        this.outline = outline;
        this.marketType = marketType;
        this.industry = industry;
        this.ranking = ranking;
        this.isActive = true;
    }
    
    public void updateStockInfo(String stockName, String outline, String marketType, Industry industry, Integer ranking) {
        this.stockName = stockName;
        this.outline = outline;
        this.marketType = marketType;
        this.industry = industry;
        this.ranking = ranking;
    }
    
    public void updateRanking(Integer ranking) {
        this.ranking = ranking;
    }
    
    public void setActive(Boolean isActive) {
        this.isActive = isActive;
    }
} 