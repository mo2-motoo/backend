package com.hsu_mafia.motoo.api.domain.financial;

import com.fasterxml.jackson.databind.JsonNode;
import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.domain.stock.StockRepository;
import com.hsu_mafia.motoo.api.domain.stock.StockApiService;
import com.hsu_mafia.motoo.global.constants.ApiConstants;
import com.hsu_mafia.motoo.global.exception.DataCollectionException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class FinancialDataCollectionService {
    
    private final StockRepository stockRepository;
    private final StockApiService stockApiService;
    private final FinancialStatementRepository financialStatementRepository;
    
    /**
     * 모든 활성화된 종목의 재무제표 데이터를 수집합니다.
     */
    public void collectFinancialData() {
        List<Stock> activeStocks = stockRepository.findActiveStocks();
        
        for (Stock stock : activeStocks) {
            try {
                collectFinancialDataForStock(stock.getStockCode());
                Thread.sleep(ApiConstants.API_CALL_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new DataCollectionException("재무제표 데이터 수집이 중단되었습니다: " + e.getMessage());
            }
        }
    }
    
    /**
     * 특정 종목의 재무제표 데이터를 수집합니다.
     */
    public void collectFinancialDataForStock(String stockCode) {
        // 분기별 재무제표 수집
        collectQuarterlyFinancialData(stockCode);
        
        // 연간 재무제표 수집
        collectAnnualFinancialData(stockCode);
    }
    

    
    /**
     * 분기별 재무제표 데이터를 수집합니다.
     */
    private void collectQuarterlyFinancialData(String stockCode) {
        JsonNode response = stockApiService.getQuarterlyFinancialData(stockCode);
        JsonNode output = response.path("output");
        
        if (output.isMissingNode() || !output.isArray()) {
            return;
        }
        
        for (JsonNode quarterData : output) {
            try {
                FinancialStatement financialStatement = parseQuarterlyFinancialData(stockCode, quarterData);
                
                // 중복 방지
                Optional<FinancialStatement> existing = financialStatementRepository
                        .findByStockAndReportDate(stockCode, financialStatement.getReportDate());
                
                if (existing.isEmpty()) {
                    financialStatementRepository.save(financialStatement);
                }
            } catch (Exception e) {
                // 개별 분기 데이터 처리 실패 시 로그만 남기고 계속 진행
                continue;
            }
        }
    }
    
    /**
     * 연간 재무제표 데이터를 수집합니다.
     */
    private void collectAnnualFinancialData(String stockCode) {
        JsonNode response = stockApiService.getAnnualFinancialData(stockCode);
        JsonNode output = response.path("output");
        
        if (output.isMissingNode() || !output.isArray()) {
            return;
        }
        
        for (JsonNode annualData : output) {
            try {
                FinancialStatement financialStatement = parseAnnualFinancialData(stockCode, annualData);
                
                // 중복 방지
                Optional<FinancialStatement> existing = financialStatementRepository
                        .findByStockAndReportDate(stockCode, financialStatement.getReportDate());
                
                if (existing.isEmpty()) {
                    financialStatementRepository.save(financialStatement);
                }
            } catch (Exception e) {
                // 개별 연간 데이터 처리 실패 시 로그만 남기고 계속 진행
                continue;
            }
        }
    }
    
    /**
     * 분기별 재무제표 데이터를 파싱합니다.
     */
    private FinancialStatement parseQuarterlyFinancialData(String stockCode, JsonNode quarterData) {
        Stock stock = stockRepository.findByStockCode(stockCode)
                .orElseThrow(() -> new DataCollectionException("종목을 찾을 수 없습니다: " + stockCode));
        
        String reportDateStr = quarterData.path("rcept_dt").asText();
        LocalDate reportDate = LocalDate.parse(reportDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 손익계산서 데이터
        Long revenue = parseLongValue(quarterData.path("thdt_sls_amt").asText());
        Long operatingIncome = parseLongValue(quarterData.path("thdt_oprt_ic").asText());
        Long netIncome = parseLongValue(quarterData.path("thdt_nt_ic").asText());
        
        // 재무상태표 데이터
        Long totalAssets = parseLongValue(quarterData.path("thdt_tot_ast").asText());
        Long totalEquity = parseLongValue(quarterData.path("thdt_tot_eq").asText());
        Long totalLiabilities = parseLongValue(quarterData.path("thdt_tot_lblt").asText());
        
        // 주식 관련 데이터
        Long totalShares = parseLongValue(quarterData.path("thdt_tot_stk_cnt").asText());
        Long outstandingShares = parseLongValue(quarterData.path("thdt_outstk_cnt").asText());
        
        return FinancialStatement.builder()
                .stock(stock)
                .reportDate(reportDate)
                .reportType("QUARTERLY")
                .revenue(revenue)
                .operatingIncome(operatingIncome)
                .netIncome(netIncome)
                .totalAssets(totalAssets)
                .totalEquity(totalEquity)
                .totalLiabilities(totalLiabilities)
                .totalShares(totalShares)
                .outstandingShares(outstandingShares)
                .build();
    }
    
    /**
     * 연간 재무제표 데이터를 파싱합니다.
     */
    private FinancialStatement parseAnnualFinancialData(String stockCode, JsonNode annualData) {
        Stock stock = stockRepository.findByStockCode(stockCode)
                .orElseThrow(() -> new DataCollectionException("종목을 찾을 수 없습니다: " + stockCode));
        
        String reportDateStr = annualData.path("rcept_dt").asText();
        LocalDate reportDate = LocalDate.parse(reportDateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 손익계산서 데이터
        Long revenue = parseLongValue(annualData.path("thdt_sls_amt").asText());
        Long operatingIncome = parseLongValue(annualData.path("thdt_oprt_ic").asText());
        Long netIncome = parseLongValue(annualData.path("thdt_nt_ic").asText());
        
        // 재무상태표 데이터
        Long totalAssets = parseLongValue(annualData.path("thdt_tot_ast").asText());
        Long totalEquity = parseLongValue(annualData.path("thdt_tot_eq").asText());
        Long totalLiabilities = parseLongValue(annualData.path("thdt_tot_lblt").asText());
        
        // 주식 관련 데이터
        Long totalShares = parseLongValue(annualData.path("thdt_tot_stk_cnt").asText());
        Long outstandingShares = parseLongValue(annualData.path("thdt_outstk_cnt").asText());
        
        return FinancialStatement.builder()
                .stock(stock)
                .reportDate(reportDate)
                .reportType("ANNUAL")
                .revenue(revenue)
                .operatingIncome(operatingIncome)
                .netIncome(netIncome)
                .totalAssets(totalAssets)
                .totalEquity(totalEquity)
                .totalLiabilities(totalLiabilities)
                .totalShares(totalShares)
                .outstandingShares(outstandingShares)
                .build();
    }
    
    /**
     * 문자열을 Long 값으로 파싱합니다.
     */
    private Long parseLongValue(String value) {
        if (value == null || value.trim().isEmpty() || "N/A".equals(value)) {
            return null;
        }
        try {
            return Long.parseLong(value.replace(",", ""));
        } catch (NumberFormatException e) {
            return null;
        }
    }
} 