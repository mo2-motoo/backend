package com.hsu_mafia.motoo.api.domain.stock;

import com.hsu_mafia.motoo.api.domain.financial.FinancialDataCollectionService;
import com.hsu_mafia.motoo.global.constants.ApiConstants;
import com.hsu_mafia.motoo.global.exception.SchedulerException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class StockSchedulerService {
    
    private final StockManagementService stockManagementService;
    private final StockDataCollectionService stockDataCollectionService;
    private final FinancialDataCollectionService financialDataCollectionService;
    
    /**
     * 1분봉 데이터 수집 스케줄러
     * 평일 09:00-15:30, 매 1분마다 실행
     */
    @Scheduled(cron = "0 * 9-15 * * MON-FRI")
    public void scheduleMinuteDataCollection() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        
        // 거래시간 체크 (09:00-15:30)
        if (currentTime.isBefore(LocalTime.of(ApiConstants.TRADING_START_HOUR, ApiConstants.TRADING_START_MINUTE)) || 
            currentTime.isAfter(LocalTime.of(ApiConstants.TRADING_END_HOUR, ApiConstants.TRADING_END_MINUTE))) {
            return;
        }
        
        stockDataCollectionService.collectMinuteData();
    }
    
    /**
     * 1시간봉 데이터 집계 스케줄러
     * 매 시간 정각에 실행
     */
    @Scheduled(cron = "0 0 * * * *")
    public void scheduleHourDataAggregation() {
        stockDataCollectionService.aggregateHourData();
    }
    
    /**
     * 1일봉 데이터 집계 스케줄러
     * 매일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleDailyDataAggregation() {
        stockDataCollectionService.aggregateDailyData();
    }
    
    /**
     * 종목 갱신 스케줄러
     * 매월 1일 자정에 실행
     */
    @Scheduled(cron = "0 0 0 1 * *")
    public void scheduleStockUpdate() {
        // KOSPI 200 종목 갱신
        stockManagementService.updateKospi200Stocks();
        
        // NASDAQ 상위 종목 갱신
        stockManagementService.updateNasdaqStocks();
    }
    
    /**
     * 재무제표 데이터 수집 스케줄러
     * 분기별로 실행 (3, 6, 9, 12월 1일 자정)
     */
    @Scheduled(cron = "0 0 0 1 3,6,9,12 *")
    public void scheduleFinancialDataCollection() {
        financialDataCollectionService.collectFinancialData();
    }
    
    /**
     * 수동 실행을 위한 메서드들
     */
    
    /**
     * 수동으로 1분봉 데이터 수집을 실행합니다.
     */
    public void manualCollectMinuteData() {
        stockDataCollectionService.collectMinuteData();
    }
    
    /**
     * 수동으로 1시간봉 데이터 집계를 실행합니다.
     */
    public void manualAggregateHourData() {
        stockDataCollectionService.aggregateHourData();
    }
    
    /**
     * 수동으로 1일봉 데이터 집계를 실행합니다.
     */
    public void manualAggregateDailyData() {
        stockDataCollectionService.aggregateDailyData();
    }
    
    /**
     * 수동으로 KOSPI 200 종목 갱신을 실행합니다.
     */
    public void manualUpdateKospi200Stocks() {
        stockManagementService.updateKospi200Stocks();
    }
    
    /**
     * 수동으로 NASDAQ 종목 갱신을 실행합니다.
     */
    public void manualUpdateNasdaqStocks() {
        stockManagementService.updateNasdaqStocks();
    }
    
    /**
     * 수동으로 재무제표 데이터 수집을 실행합니다.
     */
    public void manualCollectFinancialData() {
        financialDataCollectionService.collectFinancialData();
    }
    
    /**
     * 수동으로 특정 종목의 재무제표 데이터 수집을 실행합니다.
     */
    public void manualCollectFinancialDataForStock(String stockCode) {
        financialDataCollectionService.collectFinancialDataForStock(stockCode);
    }
} 