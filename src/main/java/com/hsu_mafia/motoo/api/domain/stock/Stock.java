package com.hsu_mafia.motoo.api.domain.stock;

import com.hsu_mafia.motoo.api.domain.stock.Industry;
import com.hsu_mafia.motoo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Stock extends BaseEntity {
    @Id
    @Column(length = 10)
    private String id; // 종목 코드 (ex. 005930)

    @Column(nullable = false, length = 50)
    private String stockName;

    @Column(length = 255)
    private String outline;

    @Column(nullable = false)
    private Long currentPrice;

    @Column(length = 20)
    private String marketType; // 예: KOSPI, NASDAQ 등

    // 연관관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id")
    private Industry industry;
} 