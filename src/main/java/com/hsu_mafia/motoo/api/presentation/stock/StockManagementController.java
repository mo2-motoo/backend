package com.hsu_mafia.motoo.api.presentation.stock;

import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.domain.stock.StockManagementService;
import com.hsu_mafia.motoo.api.domain.stock.StockSchedulerService;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock-management")
@RequiredArgsConstructor
@Tag(name = "Stock Management", description = "주식 종목 관리 및 데이터 수집 API")
public class StockManagementController {
    
    private final StockManagementService stockManagementService;
    private final StockSchedulerService stockSchedulerService;
    
    /**
     * KOSPI 200 종목 수동 갱신
     */
    @PostMapping("/stocks/kospi200")
    @Operation(summary = "KOSPI 200 종목 갱신", description = "KOSPI 200 구성종목을 수동으로 갱신합니다.")
    public ResponseEntity<CommonResponse<Void>> updateKospi200Stocks() {
        stockSchedulerService.manualUpdateKospi200Stocks();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * NASDAQ 상위 종목 수동 갱신
     */
    @PostMapping("/stocks/nasdaq")
    @Operation(summary = "NASDAQ 상위 종목 갱신", description = "NASDAQ 상위 30개 종목을 수동으로 갱신합니다.")
    public ResponseEntity<CommonResponse<Void>> updateNasdaqStocks() {
        stockSchedulerService.manualUpdateNasdaqStocks();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * 활성화된 종목 목록 조회
     */
    @GetMapping("/stocks/active")
    @Operation(summary = "활성화된 종목 조회", description = "데이터 수집이 활성화된 모든 종목을 조회합니다.")
    public ResponseEntity<CommonResponse<List<Stock>>> getActiveStocks() {
        List<Stock> activeStocks = stockManagementService.getActiveStocks();
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(activeStocks));
    }
    
    /**
     * 시장별 활성화된 종목 조회
     */
    @GetMapping("/stocks/active/{marketType}")
    @Operation(summary = "시장별 활성화된 종목 조회", description = "특정 시장의 활성화된 종목을 조회합니다.")
    public ResponseEntity<CommonResponse<List<Stock>>> getActiveStocksByMarketType(@PathVariable String marketType) {
        List<Stock> activeStocks = stockManagementService.getActiveStocksByMarketType(marketType);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(activeStocks));
    }
    
    /**
     * 종목 활성화 상태 변경
     */
    @PutMapping("/stocks/{stockCode}/active")
    @Operation(summary = "종목 활성화 상태 변경", description = "특정 종목의 데이터 수집 활성화 상태를 변경합니다.")
    public ResponseEntity<CommonResponse<Void>> toggleStockActive(
            @PathVariable String stockCode,
            @RequestParam boolean isActive) {
        stockManagementService.toggleStockActive(stockCode, isActive);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success());
    }
    
    /**
     * 1분봉 데이터 수집 수동 실행
     */
    @PostMapping("/data/collect/minute")
    @Operation(summary = "1분봉 데이터 수집", description = "활성화된 모든 종목의 1분봉 데이터를 수동으로 수집합니다.")
    public ResponseEntity<CommonResponse<Void>> collectMinuteData() {
        stockSchedulerService.manualCollectMinuteData();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * 1시간봉 데이터 집계 수동 실행
     */
    @PostMapping("/data/aggregate/hour")
    @Operation(summary = "1시간봉 데이터 집계", description = "1분봉 데이터를 집계하여 1시간봉 데이터를 생성합니다.")
    public ResponseEntity<CommonResponse<Void>> aggregateHourData() {
        stockSchedulerService.manualAggregateHourData();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * 1일봉 데이터 집계 수동 실행
     */
    @PostMapping("/data/aggregate/daily")
    @Operation(summary = "1일봉 데이터 집계", description = "1시간봉 데이터를 집계하여 1일봉 데이터를 생성합니다.")
    public ResponseEntity<CommonResponse<Void>> aggregateDailyData() {
        stockSchedulerService.manualAggregateDailyData();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * 재무제표 데이터 수집 수동 실행
     */
    @PostMapping("/data/collect/financial")
    @Operation(summary = "재무제표 데이터 수집", description = "활성화된 모든 종목의 재무제표 데이터를 수동으로 수집합니다.")
    public ResponseEntity<CommonResponse<Void>> collectFinancialData() {
        stockSchedulerService.manualCollectFinancialData();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * 특정 종목 재무제표 데이터 수집 수동 실행
     */
    @PostMapping("/data/collect/financial/{stockCode}")
    @Operation(summary = "특정 종목 재무제표 데이터 수집", description = "특정 종목의 재무제표 데이터를 수동으로 수집합니다.")
    public ResponseEntity<CommonResponse<Void>> collectFinancialDataForStock(@PathVariable String stockCode) {
        stockSchedulerService.manualCollectFinancialDataForStock(stockCode);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * 종목 존재 여부 확인
     */
    @GetMapping("/stocks/{stockCode}/exists")
    @Operation(summary = "종목 존재 여부 확인", description = "특정 종목코드가 등록되어 있는지 확인합니다.")
    public ResponseEntity<CommonResponse<Boolean>> checkStockExists(@PathVariable String stockCode) {
        boolean exists = stockManagementService.existsByStockCode(stockCode);
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(exists));
    }
    
    /**
     * E2E 테스트용 - 모든 스케줄링 작업 실행
     */
    @PostMapping("/test/e2e")
    @Operation(summary = "E2E 테스트", description = "모든 스케줄링 작업을 순차적으로 실행합니다.")
    public ResponseEntity<CommonResponse<String>> runE2ETest() {
        // 1. 종목 갱신
        stockSchedulerService.manualUpdateKospi200Stocks();
        stockSchedulerService.manualUpdateNasdaqStocks();
        
        // 2. 데이터 수집
        stockSchedulerService.manualCollectMinuteData();
        
        // 3. 데이터 집계
        stockSchedulerService.manualAggregateHourData();
        stockSchedulerService.manualAggregateDailyData();
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("E2E 테스트가 성공적으로 완료되었습니다."));
    }
} 