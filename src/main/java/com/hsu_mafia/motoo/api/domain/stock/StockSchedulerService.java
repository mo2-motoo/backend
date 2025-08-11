package com.hsu_mafia.motoo.api.domain.stock;

import com.hsu_mafia.motoo.api.domain.financial.FinancialDataCollectionService;
import com.hsu_mafia.motoo.global.constants.ApiConstants;
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
     * KOSPI 1분봉 데이터 수집 스케줄러
     * 평일 09:00-15:30, 매 1분마다 실행
     */
    @Scheduled(cron = "0 * 9-15 * * MON-FRI")
    public void scheduleKospiMinuteDataCollection() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();

        // KOSPI 거래시간 체크 (09:00-15:30)
        if (currentTime.isBefore(LocalTime.of(ApiConstants.KOSPI_TRADING_START_HOUR, ApiConstants.KOSPI_TRADING_START_MINUTE)) ||
            currentTime.isAfter(LocalTime.of(ApiConstants.KOSPI_TRADING_END_HOUR, ApiConstants.KOSPI_TRADING_END_MINUTE))) {
            return;
        }

        stockDataCollectionService.collectKospiMinuteData();
    }

    /**
     * NASDAQ 1분봉 데이터 수집 스케줄러
     * 매일 22:30-05:00(다음날), 매 1분마다 실행
     * NASDAQ은 미국 동부시간 기준 09:30-16:00 (한국시간 22:30-05:00)
     */
    @Scheduled(cron = "0 * 22-23,0-5 * * *")
    public void scheduleNasdaqMinuteDataCollection() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();

        // NASDAQ 거래시간 체크 (22:30-05:00)
        boolean isNasdaqTradingTime = false;

        // 22:30-23:59
        if (currentTime.isAfter(LocalTime.of(ApiConstants.NASDAQ_TRADING_START_HOUR, ApiConstants.NASDAQ_TRADING_START_MINUTE)) &&
            currentTime.isBefore(LocalTime.of(23, 59, 59))) {
            isNasdaqTradingTime = true;
        }
        // 00:00-05:00
        else if (currentTime.isAfter(LocalTime.of(0, 0, 0)) &&
                 currentTime.isBefore(LocalTime.of(ApiConstants.NASDAQ_TRADING_END_HOUR, ApiConstants.NASDAQ_TRADING_END_MINUTE))) {
            isNasdaqTradingTime = true;
        }

        if (!isNasdaqTradingTime) {
            return;
        }

        stockDataCollectionService.collectNasdaqMinuteData();
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
} 