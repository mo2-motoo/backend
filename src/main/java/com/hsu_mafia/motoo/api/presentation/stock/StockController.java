package com.hsu_mafia.motoo.api.presentation.stock;

import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.dto.stock.StockResponse;
import com.hsu_mafia.motoo.api.dto.stock.StockSearchRequest;
import com.hsu_mafia.motoo.api.domain.stock.StockService;
import com.hsu_mafia.motoo.api.domain.stock.StockMapper;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import com.hsu_mafia.motoo.global.util.PriceUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "주식 관련 API")
public class StockController {
    private final StockService stockService;
    private final StockMapper stockMapper;
    private final PriceUtil priceUtil;

    @GetMapping
    @Operation(summary = "주식 목록 조회", description = "모든 주식 목록을 조회합니다.")
    public ResponseEntity<CommonResponse<List<StockResponse>>> getStocks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(page, size);
        List<Stock> stocks = stockService.getStocks(userId, pageable);
        
        // PriceUtil을 통해 현재가 정보를 포함한 StockResponse 리스트 생성
        List<StockResponse> stockResponses = stocks.stream()
                .map(stock -> createStockResponseWithPriceInfo(stock))
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(stockResponses));
    }

    @GetMapping("/{stockCode}")
    @Operation(summary = "주식 상세 조회", description = "특정 주식의 상세 정보를 조회합니다.")
    public ResponseEntity<CommonResponse<StockResponse>> getStock(@PathVariable String stockCode) {
        Long userId = 1L;
        Stock stock = stockService.getStock(userId, stockCode);
        StockResponse stockResponse = createStockResponseWithPriceInfo(stock);
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(stockResponse));
    }

    @PostMapping("/search")
    @Operation(summary = "주식 검색", description = "조건에 따라 주식을 검색합니다.")
    public ResponseEntity<CommonResponse<List<StockResponse>>> searchStocks(
            @RequestBody StockSearchRequest request,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Long userId = 1L;
        Pageable pageable = PageRequest.of(page, size);
        List<Stock> stocks = stockService.searchStocks(userId, request, pageable);
        
        // PriceUtil을 통해 현재가 정보를 포함한 StockResponse 리스트 생성
        List<StockResponse> stockResponses = stocks.stream()
                .map(stock -> createStockResponseWithPriceInfo(stock))
                .collect(Collectors.toList());
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(stockResponses));
    }
    
    /**
     * Stock Entity를 PriceUtil 정보를 포함한 StockResponse로 변환하는 헬퍼 메서드
     */
    private StockResponse createStockResponseWithPriceInfo(Stock stock) {
        String stockCode = stock.getStockCode();
        
        return StockResponse.builder()
                .stockId(stock.getStockCode())
                .stockName(stock.getStockName())
                .outline(stock.getOutline())
                .marketType(stock.getMarketType())
                .industryName(stock.getIndustry() != null ? stock.getIndustry().getName() : null)
                .currentPrice(priceUtil.getCurrentPrice(stockCode).longValue())
                .priceDifference(priceUtil.getPriceDifference(stockCode))
                .rateDifference(priceUtil.getRateDifference(stockCode))
                .tradingVolume(priceUtil.getTradingVolume(stockCode))
                .min52(priceUtil.getMin52(stockCode))
                .max52(priceUtil.getMax52(stockCode))
                .per(priceUtil.getPer(stockCode))
                .pbr(priceUtil.getPbr(stockCode))
                .build();
    }
} 