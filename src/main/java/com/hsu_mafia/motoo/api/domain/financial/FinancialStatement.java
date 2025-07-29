package com.hsu_mafia.motoo.api.domain.financial;

import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
@Table(name = "financial_statements")
public class FinancialStatement extends BaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_code", referencedColumnName = "stock_code")
    private Stock stock;
    
    @Column(nullable = false)
    private LocalDate reportDate; // 재무제표 기준일
    
    @Column(nullable = false, length = 10)
    private String reportType; // "QUARTERLY" or "ANNUAL"
    
    // 손익계산서 데이터
    @Column(name = "revenue")
    private Long revenue; // 매출액
    
    @Column(name = "operating_income")
    private Long operatingIncome; // 영업이익
    
    @Column(name = "net_income")
    private Long netIncome; // 당기순이익
    
    // 재무상태표 데이터
    @Column(name = "total_assets")
    private Long totalAssets; // 총자산
    
    @Column(name = "total_equity")
    private Long totalEquity; // 자기자본
    
    @Column(name = "total_liabilities")
    private Long totalLiabilities; // 총부채
    
    // 주식 관련 데이터
    @Column(name = "total_shares")
    private Long totalShares; // 총주식수
    
    @Column(name = "outstanding_shares")
    private Long outstandingShares; // 상장주식수
    
    // 계산된 지표들
    @Column(name = "eps")
    private Double eps; // 주당순이익 (Earnings Per Share)
    
    @Column(name = "bps")
    private Double bps; // 주당순자산 (Book Value Per Share)
    
    @Column(name = "per")
    private Double per; // 주가수익비율 (Price-Earnings Ratio)
    
    @Column(name = "pbr")
    private Double pbr; // 주가순자산비율 (Price-Book Ratio)
    
    @Column(name = "roe")
    private Double roe; // 자기자본이익률 (Return on Equity)
    
    @Column(name = "debt_ratio")
    private Double debtRatio; // 부채비율
    
    /**
     * EPS 계산
     */
    public void calculateEps() {
        if (netIncome != null && outstandingShares != null && outstandingShares > 0) {
            this.eps = (double) netIncome / outstandingShares;
        }
    }
    
    /**
     * BPS 계산
     */
    public void calculateBps() {
        if (totalEquity != null && outstandingShares != null && outstandingShares > 0) {
            this.bps = (double) totalEquity / outstandingShares;
        }
    }
    
    /**
     * PER 계산 (현재가 필요)
     */
    public void calculatePer(Long currentPrice) {
        if (eps != null && eps > 0 && currentPrice != null) {
            this.per = (double) currentPrice / eps;
        }
    }
    
    /**
     * PBR 계산 (현재가 필요)
     */
    public void calculatePbr(Long currentPrice) {
        if (bps != null && bps > 0 && currentPrice != null) {
            this.pbr = (double) currentPrice / bps;
        }
    }
    
    /**
     * ROE 계산
     */
    public void calculateRoe() {
        if (netIncome != null && totalEquity != null && totalEquity > 0) {
            this.roe = ((double) netIncome / totalEquity) * 100;
        }
    }
    
    /**
     * 부채비율 계산
     */
    public void calculateDebtRatio() {
        if (totalLiabilities != null && totalEquity != null && totalEquity > 0) {
            this.debtRatio = ((double) totalLiabilities / totalEquity) * 100;
        }
    }
    
    /**
     * 모든 재무지표 계산
     */
    public void calculateAllRatios(Long currentPrice) {
        calculateEps();
        calculateBps();
        calculatePer(currentPrice);
        calculatePbr(currentPrice);
        calculateRoe();
        calculateDebtRatio();
    }
} 