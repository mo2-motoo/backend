package com.hsu_mafia.motoo.api.domain.stock;

import com.fasterxml.jackson.databind.JsonNode;
import com.hsu_mafia.motoo.api.domain.stockprice.StockPriceDaily;
import com.hsu_mafia.motoo.api.domain.stockprice.StockPriceDailyRepository;
import com.hsu_mafia.motoo.api.domain.stockprice.StockPriceHour;
import com.hsu_mafia.motoo.api.domain.stockprice.StockPriceHourRepository;
import com.hsu_mafia.motoo.api.domain.stockprice.StockPriceMinute;
import com.hsu_mafia.motoo.api.domain.stockprice.StockPriceMinuteRepository;
import com.hsu_mafia.motoo.global.constants.ApiConstants;
import com.hsu_mafia.motoo.global.exception.DataCollectionException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class StockDataCollectionService {
    
    private final StockRepository stockRepository;
    private final StockApiService stockApiService;
    private final StockPriceMinuteRepository stockPriceMinuteRepository;
    private final StockPriceHourRepository stockPriceHourRepository;
    private final StockPriceDailyRepository stockPriceDailyRepository;
    
    /**
     * 활성화된 모든 종목의 1분봉 데이터를 수집합니다.
     */
    public void collectMinuteData() {
        List<Stock> activeStocks = stockRepository.findActiveStocks();
        LocalDateTime now = LocalDateTime.now();
        
        for (Stock stock : activeStocks) {
            collectMinuteDataForStock(stock.getStockCode(), now);
            try {
                Thread.sleep(ApiConstants.API_CALL_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DataCollectionException("데이터 수집이 중단되었습니다: " + e.getMessage());
            }
        }
    }
    
    /**
     * 특정 종목의 1분봉 데이터를 수집합니다.
     */
    private void collectMinuteDataForStock(String stockCode, LocalDateTime timestamp) {
        JsonNode response = stockApiService.getStockCurrentPrice(stockCode);
        JsonNode output = response.path("output");
        
        if (output.isMissingNode()) {
            log.warn("종목 {}의 현재가 데이터를 가져올 수 없습니다.", stockCode);
            return;
        }
        
        Long currentPrice = Long.parseLong(output.path("stck_prpr").asText("0"));
        Long volume = Long.parseLong(output.path("acml_vol").asText("0"));
        Long amount = Long.parseLong(output.path("acml_tr_pbmn").asText("0"));
        
        // 1분봉 데이터 생성 (OHLC 모두 현재가로 설정)
        StockPriceMinute minuteData = StockPriceMinute.builder()
                .stockCode(stockCode)
                .timestamp(timestamp)
                .openPrice(currentPrice)
                .highPrice(currentPrice)
                .lowPrice(currentPrice)
                .closePrice(currentPrice)
                .volume(volume)
                .amount(amount)
                .build();
        
        // 중복 방지를 위해 기존 데이터 확인
        Optional<StockPriceMinute> existing = stockPriceMinuteRepository
                .findByStockCodeAndTimestamp(stockCode, timestamp);
        
        if (existing.isEmpty()) {
            stockPriceMinuteRepository.save(minuteData);
        }
    }
    
    /**
     * 1시간봉 데이터를 집계하여 생성합니다.
     */
    public void aggregateHourData() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime hourStart = now.withMinute(0).withSecond(0).withNano(0);
        LocalDateTime hourEnd = hourStart.plusHours(1);
        
        List<Stock> activeStocks = stockRepository.findActiveStocks();
        
        for (Stock stock : activeStocks) {
            aggregateHourDataForStock(stock.getStockCode(), hourStart, hourEnd);
        }
    }
    
    /**
     * 특정 종목의 1시간봉 데이터를 집계합니다.
     */
    private void aggregateHourDataForStock(String stockCode, LocalDateTime hourStart, LocalDateTime hourEnd) {
        List<StockPriceMinute> minuteDataList = stockPriceMinuteRepository
                .findByStockCodeAndTimestampBetween(stockCode, hourStart, hourEnd);
        
        if (minuteDataList.isEmpty()) {
            return;
        }
        
        // OHLCV 계산
        Long openPrice = minuteDataList.get(0).getOpenPrice();
        Long closePrice = minuteDataList.get(minuteDataList.size() - 1).getClosePrice();
        Long highPrice = minuteDataList.stream().mapToLong(StockPriceMinute::getHighPrice).max().orElse(0L);
        Long lowPrice = minuteDataList.stream().mapToLong(StockPriceMinute::getLowPrice).min().orElse(0L);
        Long volume = minuteDataList.stream().mapToLong(StockPriceMinute::getVolume).sum();
        Long amount = minuteDataList.stream().mapToLong(StockPriceMinute::getAmount).sum();
        
        StockPriceHour hourData = StockPriceHour.builder()
                .stockCode(stockCode)
                .timestamp(hourStart)
                .openPrice(openPrice)
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .closePrice(closePrice)
                .volume(volume)
                .amount(amount)
                .build();
        
        // 중복 방지
        Optional<StockPriceHour> existing = stockPriceHourRepository
                .findByStockCodeAndTimestamp(stockCode, hourStart);
        
        if (existing.isEmpty()) {
            stockPriceHourRepository.save(hourData);
        }
    }
    
    /**
     * 1일봉 데이터를 집계하여 생성합니다.
     */
    public void aggregateDailyData() {
        LocalDate today = LocalDate.now();
        LocalDateTime dayStart = today.atStartOfDay();
        LocalDateTime dayEnd = today.atTime(LocalTime.MAX);
        
        List<Stock> activeStocks = stockRepository.findActiveStocks();
        
        for (Stock stock : activeStocks) {
            aggregateDailyDataForStock(stock.getStockCode(), today, dayStart, dayEnd);
        }
    }
    
    /**
     * 특정 종목의 1일봉 데이터를 집계합니다.
     */
    private void aggregateDailyDataForStock(String stockCode, LocalDate date, LocalDateTime dayStart, LocalDateTime dayEnd) {
        List<StockPriceHour> hourDataList = stockPriceHourRepository
                .findByStockCodeAndTimestampBetween(stockCode, dayStart, dayEnd);
        
        if (hourDataList.isEmpty()) {
            return;
        }
        
        // OHLCV 계산
        Long openPrice = hourDataList.get(0).getOpenPrice();
        Long closePrice = hourDataList.get(hourDataList.size() - 1).getClosePrice();
        Long highPrice = hourDataList.stream().mapToLong(StockPriceHour::getHighPrice).max().orElse(0L);
        Long lowPrice = hourDataList.stream().mapToLong(StockPriceHour::getLowPrice).min().orElse(0L);
        Long volume = hourDataList.stream().mapToLong(StockPriceHour::getVolume).sum();
        Long amount = hourDataList.stream().mapToLong(StockPriceHour::getAmount).sum();
        
        StockPriceDaily dailyData = StockPriceDaily.builder()
                .stockCode(stockCode)
                .date(date)
                .openPrice(openPrice)
                .highPrice(highPrice)
                .lowPrice(lowPrice)
                .closePrice(closePrice)
                .volume(volume)
                .amount(amount)
                .build();
        
        // 중복 방지
        Optional<StockPriceDaily> existing = stockPriceDailyRepository
                .findByStockCodeAndDate(stockCode, date);
        
        if (existing.isEmpty()) {
            stockPriceDailyRepository.save(dailyData);
        }
    }
} 