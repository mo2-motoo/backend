package com.hsu_mafia.motoo.api.domain.stock;

import com.fasterxml.jackson.databind.JsonNode;
import com.hsu_mafia.motoo.api.domain.stock.Industry;
import com.hsu_mafia.motoo.api.domain.stock.StockRepository;
import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import com.hsu_mafia.motoo.global.exception.StockApiException;
import com.hsu_mafia.motoo.global.constants.ApiConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockManagementService {
    
    private final StockRepository stockRepository;
    private final StockApiService stockApiService;
    
    /**
     * KOSPI 200 상위 종목들을 수동으로 갱신합니다.
     */
    public void updateKospi200Stocks() {
        JsonNode response = stockApiService.getKospi200Stocks();
        JsonNode output = response.path("output");
        
        if (output.isMissingNode() || !output.isArray()) {
            throw new StockApiException("KOSPI 200 종목 데이터를 가져올 수 없습니다.");
        }
        
        List<Stock> stocksToSave = new ArrayList<>();
        int ranking = 1;
        
        for (JsonNode stockNode : output) {
            String stockCode = stockNode.path("hts_kor_isnm").asText();
            String stockName = stockNode.path("hts_kor_isnm").asText();
            
            if (stockCode != null && !stockCode.trim().isEmpty()) {
                Stock stock = Stock.builder()
                        .stockCode(stockCode)
                        .stockName(stockName)
                        .marketType("KOSPI")
                        .outline("KOSPI 200 구성종목")
                        .ranking(ranking++)
                        .build();
                
                stocksToSave.add(stock);
            }
        }
        
        // 기존 종목들과 병합하여 저장
        saveOrUpdateStocksWithRanking(stocksToSave);
    }
    
    /**
     * NASDAQ 상위 종목들을 API를 통해 갱신합니다.
     */
    public void updateNasdaqStocks() {
        JsonNode response = stockApiService.getNasdaqTopStocks();
        JsonNode output = response.path("output");
        
        if (output.isMissingNode() || !output.isArray()) {
            throw new StockApiException("NASDAQ 상위 종목 데이터를 가져올 수 없습니다.");
        }
        
        List<Stock> stocksToSave = new ArrayList<>();
        int ranking = 1;
        
        for (JsonNode stockNode : output) {
            String stockCode = stockNode.path("hts_kor_isnm").asText();
            String stockName = stockNode.path("hts_kor_isnm").asText();
            
            if (stockCode != null && !stockCode.trim().isEmpty()) {
                Stock stock = Stock.builder()
                        .stockCode(stockCode)
                        .stockName(stockName)
                        .marketType("NASDAQ")
                        .outline("NASDAQ 상위 종목")
                        .ranking(ranking++)
                        .build();
                
                stocksToSave.add(stock);
            }
        }
        
        // 기존 종목들과 병합하여 저장
        saveOrUpdateStocksWithRanking(stocksToSave);
    }
    
    /**
     * 종목들을 저장하거나 업데이트합니다.
     */
    private void saveOrUpdateStocks(List<Stock> stocks) {
        for (Stock stock : stocks) {
            Optional<Stock> existingStock = stockRepository.findByStockCode(stock.getStockCode());
            
            if (existingStock.isPresent()) {
                // 기존 종목 정보 업데이트
                Stock existing = existingStock.get();
                existing.updateStockInfo(stock.getStockName(), stock.getOutline(), 
                                       stock.getMarketType(), stock.getIndustry(), stock.getRanking());
                stockRepository.save(existing);
            } else {
                // 새로운 종목 저장
                stockRepository.save(stock);
            }
        }
    }

    /**
     * 랭킹 정보를 포함하여 종목들을 저장하거나 업데이트합니다.
     */
    private void saveOrUpdateStocksWithRanking(List<Stock> stocks) {
        for (Stock stock : stocks) {
            Optional<Stock> existingStock = stockRepository.findByStockCode(stock.getStockCode());
            
            if (existingStock.isPresent()) {
                // 기존 종목 정보 업데이트 (랭킹 포함)
                Stock existing = existingStock.get();
                existing.updateStockInfo(stock.getStockName(), stock.getOutline(), 
                                       stock.getMarketType(), stock.getIndustry(), stock.getRanking());
                stockRepository.save(existing);
            } else {
                // 새로운 종목 저장
                stockRepository.save(stock);
            }
        }
    }
    
    /**
     * 특정 종목의 활성화 상태를 변경합니다.
     */
    public void toggleStockActive(String stockCode, boolean isActive) {
        Optional<Stock> stockOpt = stockRepository.findByStockCode(stockCode);
        
        if (stockOpt.isEmpty()) {
            throw new BaseException(ErrorCode.STOCK_NOT_FOUND);
        }
        
        Stock stock = stockOpt.get();
        stock.setActive(isActive);
        stockRepository.save(stock);
    }
    
    /**
     * 모든 활성화된 종목을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<Stock> getActiveStocks() {
        return stockRepository.findActiveStocks();
    }
    
    /**
     * 특정 시장의 활성화된 종목을 조회합니다.
     */
    @Transactional(readOnly = true)
    public List<Stock> getActiveStocksByMarketType(String marketType) {
        return stockRepository.findActiveStocksByMarketType(marketType);
    }
    
    /**
     * 종목이 존재하는지 확인합니다.
     */
    @Transactional(readOnly = true)
    public boolean existsByStockCode(String stockCode) {
        return stockRepository.existsByStockCode(stockCode);
    }
} 