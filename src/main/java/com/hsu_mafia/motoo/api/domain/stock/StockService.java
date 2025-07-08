package com.hsu_mafia.motoo.api.domain.stock;

import com.hsu_mafia.motoo.api.dto.stock.StockResponse;
import com.hsu_mafia.motoo.api.dto.stock.StockSearchRequest;
import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import com.hsu_mafia.motoo.global.util.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {
    private final StockRepository stockRepository;
    private final PriceUtil priceUtil;

    public List<StockResponse> getStocks(Long userId) {
        List<Stock> stocks = stockRepository.findAll();
        
        return stocks.stream()
                .map(this::convertToStockResponse)
                .collect(Collectors.toList());
    }

    public StockResponse getStock(Long userId, String stockId) {
        Stock stock = stockRepository.findById(stockId)
                .orElseThrow(() -> new BaseException(ErrorCode.STOCK_NOT_FOUND));
        
        return convertToStockResponse(stock);
    }

    public List<StockResponse> searchStocks(Long userId, StockSearchRequest request) {
        List<Stock> stocks;
        
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            // 키워드 검색
            stocks = stockRepository.findByStockNameContainingIgnoreCaseOrIdContainingIgnoreCase(
                    request.getKeyword(), request.getKeyword());
        } else if (request.getMarketType() != null && !request.getMarketType().trim().isEmpty()) {
            // 시장 타입 검색
            stocks = stockRepository.findByMarketType(request.getMarketType());
        } else if (request.getIndustryName() != null && !request.getIndustryName().trim().isEmpty()) {
            // 산업별 검색
            stocks = stockRepository.findByIndustryNameContainingIgnoreCase(request.getIndustryName());
        } else {
            // 검색 조건이 없으면 전체 조회
            stocks = stockRepository.findAll();
        }
        
        return stocks.stream()
                .map(this::convertToStockResponse)
                .collect(Collectors.toList());
    }

    private StockResponse convertToStockResponse(Stock stock) {
        String stockId = stock.getId();
        
        return StockResponse.builder()
                .stockId(stockId)
                .stockName(stock.getStockName())
                .outline(stock.getOutline())
                .currentPrice(priceUtil.getCurrentPrice(stockId).longValue())
                .marketType(stock.getMarketType())
                .industryName(stock.getIndustry() != null ? stock.getIndustry().getName() : null)
                .priceDifference(priceUtil.getPriceDifference(stockId))
                .rateDifference(priceUtil.getRateDifference(stockId))
                .tradingVolume(priceUtil.getTradingVolume(stockId))
                .min52(priceUtil.getMin52(stockId))
                .max52(priceUtil.getMax52(stockId))
                .per(priceUtil.getPer(stockId))
                .pbr(priceUtil.getPbr(stockId))
                .build();
    }
} 