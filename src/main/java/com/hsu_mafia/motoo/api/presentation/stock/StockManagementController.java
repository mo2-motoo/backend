package com.hsu_mafia.motoo.api.presentation.stock;

import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.domain.stock.StockManagementService;
import com.hsu_mafia.motoo.api.domain.stock.StockDataCollectionService;
import com.hsu_mafia.motoo.api.domain.financial.FinancialDataCollectionService;
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
    private final StockDataCollectionService stockDataCollectionService;
    private final FinancialDataCollectionService financialDataCollectionService;
    
    /**
     * KOSPI 200 종목 수동 갱신
     */
    @PostMapping("/stocks/kospi200")
    @Operation(summary = "KOSPI 200 종목 갱신", description = "KOSPI 200 구성종목을 수동으로 갱신합니다.")
    public ResponseEntity<CommonResponse<Void>> updateKospi200Stocks() {
        stockManagementService.updateKospi200Stocks();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * NASDAQ 상위 종목 수동 갱신
     */
    @PostMapping("/stocks/nasdaq")
    @Operation(summary = "NASDAQ 상위 종목 갱신", description = "NASDAQ 상위 30개 종목을 수동으로 갱신합니다.")
    public ResponseEntity<CommonResponse<Void>> updateNasdaqStocks() {
        stockManagementService.updateNasdaqStocks();
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
    @PostMapping("/data/collect/kospi-minute")
    @Operation(summary = "KOSPI 1분봉 데이터 수집", description = "KOSPI 종목의 1분봉 데이터를 수동으로 수집합니다.")
    public ResponseEntity<CommonResponse<Void>> collectKospiMinuteData() {
        stockDataCollectionService.collectKospiMinuteData();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * NASDAQ 1분봉 데이터 수집 수동 실행
     */
    @PostMapping("/data/collect/nasdaq-minute")
    @Operation(summary = "NASDAQ 1분봉 데이터 수집", description = "NASDAQ 종목의 1분봉 데이터를 수동으로 수집합니다.")
    public ResponseEntity<CommonResponse<Void>> collectNasdaqMinuteData() {
        stockDataCollectionService.collectNasdaqMinuteData();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * 1시간봉 데이터 집계 수동 실행
     */
    @PostMapping("/data/aggregate/hour")
    @Operation(summary = "1시간봉 데이터 집계", description = "1분봉 데이터를 집계하여 1시간봉 데이터를 생성합니다.")
    public ResponseEntity<CommonResponse<Void>> aggregateHourData() {
        stockDataCollectionService.aggregateHourData();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * 1일봉 데이터 집계 수동 실행
     */
    @PostMapping("/data/aggregate/daily")
    @Operation(summary = "1일봉 데이터 집계", description = "1시간봉 데이터를 집계하여 1일봉 데이터를 생성합니다.")
    public ResponseEntity<CommonResponse<Void>> aggregateDailyData() {
        stockDataCollectionService.aggregateDailyData();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * 재무제표 데이터 수집 수동 실행
     */
    @PostMapping("/data/collect/financial")
    @Operation(summary = "재무제표 데이터 수집", description = "활성화된 모든 종목의 재무제표 데이터를 수동으로 수집합니다.")
    public ResponseEntity<CommonResponse<Void>> collectFinancialData() {
        financialDataCollectionService.collectFinancialData();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success());
    }
    
    /**
     * 특정 종목 재무제표 데이터 수집 수동 실행
     */
    @PostMapping("/data/collect/financial/{stockCode}")
    @Operation(summary = "특정 종목 재무제표 데이터 수집", description = "특정 종목의 재무제표 데이터를 수동으로 수집합니다.")
    public ResponseEntity<CommonResponse<Void>> collectFinancialDataForStock(@PathVariable String stockCode) {
        financialDataCollectionService.collectFinancialDataForStock(stockCode);
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
     * E2E 테스트용 - 종목 갱신 테스트
     */
    @PostMapping("/test/update-stocks")
    @Operation(summary = "종목 갱신 테스트", description = "KOSPI 200과 NASDAQ 종목 갱신을 테스트합니다.")
    public ResponseEntity<CommonResponse<String>> testUpdateStocks() {
        // 1. 종목 갱신
        stockManagementService.updateKospi200Stocks();
        stockManagementService.updateNasdaqStocks();
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("종목 갱신 테스트가 성공적으로 완료되었습니다."));
    }
    
    /**
     * E2E 테스트용 - KOSPI 데이터 수집 테스트
     */
    @PostMapping("/test/kospi-data-collection")
    @Operation(summary = "KOSPI 데이터 수집 테스트", description = "KOSPI 종목의 데이터 수집 및 집계를 테스트합니다.")
    public ResponseEntity<CommonResponse<String>> testKospiDataCollection() {
        // 1. KOSPI 1분봉 데이터 수집
        stockDataCollectionService.collectKospiMinuteData();
        
        // 2. 1시간봉 데이터 집계
        stockDataCollectionService.aggregateHourData();
        
        // 3. 1일봉 데이터 집계
        stockDataCollectionService.aggregateDailyData();
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("KOSPI 데이터 수집 테스트가 성공적으로 완료되었습니다."));
    }
    
    /**
     * E2E 테스트용 - NASDAQ 데이터 수집 테스트
     */
    @PostMapping("/test/nasdaq-data-collection")
    @Operation(summary = "NASDAQ 데이터 수집 테스트", description = "NASDAQ 종목의 데이터 수집 및 집계를 테스트합니다.")
    public ResponseEntity<CommonResponse<String>> testNasdaqDataCollection() {
        // 1. NASDAQ 1분봉 데이터 수집
        stockDataCollectionService.collectNasdaqMinuteData();
        
        // 2. 1시간봉 데이터 집계
        stockDataCollectionService.aggregateHourData();
        
        // 3. 1일봉 데이터 집계
        stockDataCollectionService.aggregateDailyData();
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("NASDAQ 데이터 수집 테스트가 성공적으로 완료되었습니다."));
    }
    
    /**
     * E2E 테스트용 - 재무제표 데이터 수집 테스트
     */
    @PostMapping("/test/financial-data-collection")
    @Operation(summary = "재무제표 데이터 수집 테스트", description = "재무제표 데이터 수집을 테스트합니다.")
    public ResponseEntity<CommonResponse<String>> testFinancialDataCollection() {
        // 재무제표 데이터 수집
        financialDataCollectionService.collectFinancialData();
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("재무제표 데이터 수집 테스트가 성공적으로 완료되었습니다."));
    }
    
    /**
     * E2E 테스트용 - 전체 시스템 테스트 (주의: 모든 기능을 순차적으로 실행)
     */
    @PostMapping("/test/full-system")
    @Operation(summary = "전체 시스템 테스트", description = "모든 스케줄링 작업을 순차적으로 실행합니다. (주의: 장 시간과 무관하게 실행)")
    public ResponseEntity<CommonResponse<String>> testFullSystem() {
        // 1. 종목 갱신
        stockManagementService.updateKospi200Stocks();
        stockManagementService.updateNasdaqStocks();
        
        // 2. 데이터 수집 (장 시간과 무관하게 실행)
        stockDataCollectionService.collectKospiMinuteData();
        stockDataCollectionService.collectNasdaqMinuteData();
        
        // 3. 데이터 집계
        stockDataCollectionService.aggregateHourData();
        stockDataCollectionService.aggregateDailyData();
        
        // 4. 재무제표 데이터 수집
        financialDataCollectionService.collectFinancialData();
        
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success("전체 시스템 테스트가 성공적으로 완료되었습니다. (주의: 장 시간과 무관하게 실행됨)"));
    }
} 