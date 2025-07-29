package com.hsu_mafia.motoo.global.util;

import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.domain.stock.StockRepository;
import com.hsu_mafia.motoo.api.domain.stockprice.StockPriceDaily;
import com.hsu_mafia.motoo.api.domain.stockprice.StockPriceDailyRepository;
import com.hsu_mafia.motoo.api.domain.stockprice.StockPriceHour;
import com.hsu_mafia.motoo.api.domain.stockprice.StockPriceHourRepository;
import com.hsu_mafia.motoo.api.domain.stockprice.StockPriceMinute;
import com.hsu_mafia.motoo.api.domain.stockprice.StockPriceMinuteRepository;
import com.hsu_mafia.motoo.api.domain.financial.FinancialStatement;
import com.hsu_mafia.motoo.api.domain.financial.FinancialStatementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PriceUtil {
    
    private final StockRepository stockRepository;
    private final StockPriceMinuteRepository stockPriceMinuteRepository;
    private final StockPriceHourRepository stockPriceHourRepository;
    private final StockPriceDailyRepository stockPriceDailyRepository;
    private final FinancialStatementRepository financialStatementRepository;

    /**
     * 종목의 인덱스를 반환합니다 (랭킹 기반)
     */
    public Integer getStockIndex(String stockCode) {
        Optional<Stock> stock = stockRepository.findByStockCode(stockCode);
        return stock.map(Stock::getRanking).orElse(null);
    }

    /**
     * 종목의 현재가를 반환합니다 (최신 1분봉 데이터 기준)
     */
    public Long getCurrentPrice(String stockCode) {
        Optional<StockPriceMinute> latestMinute = stockPriceMinuteRepository
                .findTopByStockCodeOrderByTimestampDesc(stockCode);
        
        if (latestMinute.isPresent()) {
            return latestMinute.get().getClosePrice();
        }
        
        // 1분봉이 없으면 1시간봉에서 조회
        Optional<StockPriceHour> latestHour = stockPriceHourRepository
                .findTopByStockCodeOrderByTimestampDesc(stockCode);
        
        if (latestHour.isPresent()) {
            return latestHour.get().getClosePrice();
        }
        
        // 1시간봉도 없으면 1일봉에서 조회
        Optional<StockPriceDaily> latestDaily = stockPriceDailyRepository
                .findTopByStockCodeOrderByDateDesc(stockCode);
        
        return latestDaily.map(StockPriceDaily::getClosePrice).orElse(0L);
    }

    /**
     * 종목의 전일 대비 가격 변동을 반환합니다
     */
    public String getPriceDifference(String stockCode) {
        Long currentPrice = getCurrentPrice(stockCode);
        Long previousPrice = getPreviousDayClosePrice(stockCode);
        
        if (previousPrice == 0L) {
            return "0";
        }
        
        long difference = currentPrice - previousPrice;
        return String.valueOf(difference);
    }

    /**
     * 종목의 전일 대비 등락률을 반환합니다
     */
    public String getRateDifference(String stockCode) {
        Long currentPrice = getCurrentPrice(stockCode);
        Long previousPrice = getPreviousDayClosePrice(stockCode);
        
        if (previousPrice == 0L) {
            return "0.00";
        }
        
        double rate = ((double) (currentPrice - previousPrice) / previousPrice) * 100;
        return String.format("%.2f", rate);
    }

    /**
     * 종목의 거래량을 반환합니다 (최신 1분봉 데이터 기준)
     */
    public Long getTradingVolume(String stockCode) {
        Optional<StockPriceMinute> latestMinute = stockPriceMinuteRepository
                .findTopByStockCodeOrderByTimestampDesc(stockCode);
        
        if (latestMinute.isPresent()) {
            return latestMinute.get().getVolume();
        }
        
        // 1분봉이 없으면 1시간봉에서 조회
        Optional<StockPriceHour> latestHour = stockPriceHourRepository
                .findTopByStockCodeOrderByTimestampDesc(stockCode);
        
        if (latestHour.isPresent()) {
            return latestHour.get().getVolume();
        }
        
        // 1시간봉도 없으면 1일봉에서 조회
        Optional<StockPriceDaily> latestDaily = stockPriceDailyRepository
                .findTopByStockCodeOrderByDateDesc(stockCode);
        
        return latestDaily.map(StockPriceDaily::getVolume).orElse(0L);
    }

    /**
     * 종목의 52주 최저가를 반환합니다 (최근 1년간의 1일봉 데이터 기준)
     */
    public Integer getMin52(String stockCode) {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        
        Optional<StockPriceDaily> minPrice = stockPriceDailyRepository
                .findTopByStockCodeAndDateGreaterThanEqualOrderByLowPriceAsc(stockCode, oneYearAgo);
        
        return minPrice.map(daily -> daily.getLowPrice().intValue()).orElse(0);
    }

    /**
     * 종목의 52주 최고가를 반환합니다 (최근 1년간의 1일봉 데이터 기준)
     */
    public Integer getMax52(String stockCode) {
        LocalDate oneYearAgo = LocalDate.now().minusYears(1);
        
        Optional<StockPriceDaily> maxPrice = stockPriceDailyRepository
                .findTopByStockCodeAndDateGreaterThanEqualOrderByHighPriceDesc(stockCode, oneYearAgo);
        
        return maxPrice.map(daily -> daily.getHighPrice().intValue()).orElse(0);
    }

    /**
     * 종목의 PER(주가수익비율)을 반환합니다
     */
    public String getPer(String stockCode) {
        Long currentPrice = getCurrentPrice(stockCode);
        if (currentPrice == 0L) {
            return "N/A";
        }
        
        Optional<FinancialStatement> latestFinancial = financialStatementRepository
                .findTopByStockOrderByReportDateDesc(stockCode);
        
        if (latestFinancial.isPresent()) {
            FinancialStatement financial = latestFinancial.get();
            financial.calculatePer(currentPrice);
            
            if (financial.getPer() != null && financial.getPer() > 0) {
                return String.format("%.2f", financial.getPer());
            }
        }
        
        return "N/A";
    }

    /**
     * 종목의 PBR(주가순자산비율)을 반환합니다
     */
    public String getPbr(String stockCode) {
        Long currentPrice = getCurrentPrice(stockCode);
        if (currentPrice == 0L) {
            return "N/A";
        }
        
        Optional<FinancialStatement> latestFinancial = financialStatementRepository
                .findTopByStockOrderByReportDateDesc(stockCode);
        
        if (latestFinancial.isPresent()) {
            FinancialStatement financial = latestFinancial.get();
            financial.calculatePbr(currentPrice);
            
            if (financial.getPbr() != null && financial.getPbr() > 0) {
                return String.format("%.2f", financial.getPbr());
            }
        }
        
        return "N/A";
    }

    /**
     * 전일 종가를 조회합니다
     */
    private Long getPreviousDayClosePrice(String stockCode) {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        
        Optional<StockPriceDaily> previousDay = stockPriceDailyRepository
                .findByStockCodeAndDate(stockCode, yesterday);
        
        return previousDay.map(StockPriceDaily::getClosePrice).orElse(0L);
    }
}
