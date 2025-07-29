package com.hsu_mafia.motoo.api.domain.stock;

import com.hsu_mafia.motoo.api.dto.stock.StockSearchRequest;
import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import com.hsu_mafia.motoo.global.util.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StockService {
    private final StockRepository stockRepository;
    private final PriceUtil priceUtil;

    public List<Stock> getStocks(Long userId, Pageable pageable) {
        return stockRepository.findAll(pageable).getContent();
    }

    public Stock getStock(Long userId, String stockCode) {
        return stockRepository.findByStockCode(stockCode)
                .orElseThrow(() -> new BaseException(ErrorCode.STOCK_NOT_FOUND));
    }

    public List<Stock> searchStocks(Long userId, StockSearchRequest request, Pageable pageable) {
        if (request.getKeyword() != null && !request.getKeyword().trim().isEmpty()) {
            // 키워드 검색
            return stockRepository.findByKeyword(request.getKeyword(), pageable);
        } else if (request.getMarketType() != null && !request.getMarketType().trim().isEmpty()) {
            // 시장 타입 검색
            return stockRepository.findByMarketType(request.getMarketType(), pageable);
        } else if (request.getIndustryName() != null && !request.getIndustryName().trim().isEmpty()) {
            // 산업별 검색
            return stockRepository.findByIndustry(request.getIndustryName(), pageable);
        } else {
            // 검색 조건이 없으면 전체 조회
            return (List<Stock>) stockRepository.findAll(pageable);
        }
    }
    
    public Optional<Stock> findByStockCode(String stockCode) {
        return stockRepository.findByStockCode(stockCode);
    }
} 