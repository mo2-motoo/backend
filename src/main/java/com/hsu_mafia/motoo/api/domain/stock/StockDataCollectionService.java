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

import java.math.BigDecimal;
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
     * KOSPI 종목의 1분봉 데이터를 수집합니다.
     */
    public void collectKospiMinuteData() {
        List<Stock> kospiStocks = stockRepository.findActiveStocksByMarketType(ApiConstants.MARKET_TYPE_KOSPI);
        LocalDateTime now = LocalDateTime.now();
        
        for (Stock stock : kospiStocks) {
            collectKospiMinuteDataForStock(stock.getStockCode(), now);
            try {
                Thread.sleep(ApiConstants.API_CALL_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DataCollectionException("KOSPI 데이터 수집이 중단되었습니다: " + e.getMessage());
            }
        }
    }
    
    /**
     * NASDAQ 종목의 1분봉 데이터를 수집합니다.
     */
    public void collectNasdaqMinuteData() {
        List<Stock> nasdaqStocks = stockRepository.findActiveStocksByMarketType(ApiConstants.MARKET_TYPE_NASDAQ);
        LocalDateTime now = LocalDateTime.now();
        
        for (Stock stock : nasdaqStocks) {
            collectNasdaqMinuteDataForStock(stock.getStockCode(), now);
            try {
                Thread.sleep(ApiConstants.API_CALL_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DataCollectionException("NASDAQ 데이터 수집이 중단되었습니다: " + e.getMessage());
            }
        }
    }
    
    /**
     * 특정 KOSPI 종목의 1분봉 데이터를 수집합니다.
     */
    private void collectKospiMinuteDataForStock(String stockCode, LocalDateTime timestamp) {
        JsonNode response = stockApiService.getStockCurrentPrice(stockCode);
        JsonNode output = response.path("output");
        
        if (output.isMissingNode()) {
            log.warn("KOSPI 종목 {}의 현재가 데이터를 가져올 수 없습니다.", stockCode);
            return;
        }
        
        // 소수점이 포함된 가격 데이터를 BigDecimal로 파싱
        BigDecimal currentPrice = parsePriceToBigDecimal(output.path("stck_prpr").asText("0"));
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
     * 특정 NASDAQ 종목의 1분봉 데이터를 수집합니다.
     */
    private void collectNasdaqMinuteDataForStock(String stockCode, LocalDateTime timestamp) {
        JsonNode response = stockApiService.getOverseasStockCurrentPrice(stockCode);
        JsonNode output = response.path("output");
        
        if (output.isMissingNode()) {
            log.warn("NASDAQ 종목 {}의 현재가 데이터를 가져올 수 없습니다.", stockCode);
            return;
        }
        
        // 해외주식 API 응답 파싱 - 소수점이 포함된 가격 데이터를 BigDecimal로 처리
        BigDecimal currentPrice = parsePriceToBigDecimal(output.path("last").asText("0"));
        Long volume = Long.parseLong(output.path("volume").asText("0"));
        Long amount = Long.parseLong(output.path("amount").asText("0"));
        
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
     * 가격 문자열을 BigDecimal로 변환하는 헬퍼 메서드
     * 소수점이 포함된 가격 데이터를 정확하게 처리합니다.
     */
    private BigDecimal parsePriceToBigDecimal(String priceStr) {
        try {
            if (priceStr == null || priceStr.trim().isEmpty() || "0".equals(priceStr)) {
                return BigDecimal.ZERO;
            }
            // BigDecimal로 정확한 소수점 계산
            return new BigDecimal(priceStr);
        } catch (NumberFormatException e) {
            log.warn("가격 파싱 실패: {}, 기본값 0 사용", priceStr);
            return BigDecimal.ZERO;
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
        BigDecimal openPrice = minuteDataList.get(0).getOpenPrice();
        BigDecimal closePrice = minuteDataList.get(minuteDataList.size() - 1).getClosePrice();
        BigDecimal highPrice = minuteDataList.stream().map(StockPriceMinute::getHighPrice).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal lowPrice = minuteDataList.stream().map(StockPriceMinute::getLowPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
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
        BigDecimal openPrice = hourDataList.get(0).getOpenPrice();
        BigDecimal closePrice = hourDataList.get(hourDataList.size() - 1).getClosePrice();
        BigDecimal highPrice = hourDataList.stream().map(StockPriceHour::getHighPrice).max(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
        BigDecimal lowPrice = hourDataList.stream().map(StockPriceHour::getLowPrice).min(BigDecimal::compareTo).orElse(BigDecimal.ZERO);
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