package com.hsu_mafia.motoo.api.domain.execution;

import com.hsu_mafia.motoo.global.constants.ApiConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderExecutionScheduler {
    
    private final OrderExecutionService orderExecutionService;
    
    /**
     * 주문 체결 처리 스케줄러
     * 거래시간 중 매 1분마다 실행
     */
    @Scheduled(cron = "0 * 9-15 * * MON-FRI") // KOSPI 거래시간
    public void scheduleOrderExecution() {
        LocalDateTime now = LocalDateTime.now();
        LocalTime currentTime = now.toLocalTime();
        
        // KOSPI 거래시간 체크 (09:00-15:30)
        if (currentTime.isBefore(LocalTime.of(ApiConstants.KOSPI_TRADING_START_HOUR, ApiConstants.KOSPI_TRADING_START_MINUTE)) || 
            currentTime.isAfter(LocalTime.of(ApiConstants.KOSPI_TRADING_END_HOUR, ApiConstants.KOSPI_TRADING_END_MINUTE))) {
            return;
        }
        
        log.info("주문 체결 처리 시작 - {}", now);
        orderExecutionService.processPendingOrders();
    }
    
    /**
     * NASDAQ 주문 체결 처리 스케줄러
     * NASDAQ 거래시간 중 매 1분마다 실행
     */
    @Scheduled(cron = "0 * 22-23,0-5 * * *") // NASDAQ 거래시간
    public void scheduleNasdaqOrderExecution() {
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
        
        log.info("NASDAQ 주문 체결 처리 시작 - {}", now);
        orderExecutionService.processPendingOrders();
    }
} 