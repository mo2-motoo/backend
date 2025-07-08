package com.hsu_mafia.motoo.api.presentation.stock;

import com.hsu_mafia.motoo.api.dto.stock.StockResponse;
import com.hsu_mafia.motoo.api.dto.stock.StockSearchRequest;
import com.hsu_mafia.motoo.api.domain.stock.StockService;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
@Tag(name = "Stock", description = "주식 관련 API")
public class StockController {
    private final StockService stockService;

    @GetMapping
    @Operation(summary = "주식 목록 조회", description = "모든 주식 목록을 조회합니다.")
    public CommonResponse<List<StockResponse>> getStocks() {
        Long userId = 1L;
        return CommonResponse.success(stockService.getStocks(userId));
    }

    @GetMapping("/{stockId}")
    @Operation(summary = "주식 상세 조회", description = "특정 주식의 상세 정보를 조회합니다.")
    public CommonResponse<StockResponse> getStock(@PathVariable String stockId) {
        Long userId = 1L;
        return CommonResponse.success(stockService.getStock(userId, stockId));
    }

    @PostMapping("/search")
    @Operation(summary = "주식 검색", description = "조건에 따라 주식을 검색합니다.")
    public CommonResponse<List<StockResponse>> searchStocks(@RequestBody StockSearchRequest request) {
        Long userId = 1L;
        return CommonResponse.success(stockService.searchStocks(userId, request));
    }
} 