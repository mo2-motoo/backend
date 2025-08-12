package com.hsu_mafia.motoo.api.application;

import com.hsu_mafia.motoo.api.domain.stock.Industry;
import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.domain.stock.StockRepository;
import com.hsu_mafia.motoo.api.domain.stock.IndustryRepository;
import com.hsu_mafia.motoo.api.domain.stock.StockManagementService;
import com.hsu_mafia.motoo.api.domain.token.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitDataLoader implements CommandLineRunner {
    
    private final StockRepository stockRepository;
    private final IndustryRepository industryRepository;
    private final TokenService tokenService;
    private final StockManagementService stockManagementService;
    
    @Override
    public void run(String... args) throws Exception {
        log.info("초기 데이터 로딩을 시작합니다.");
        
        // 1. 한국투자증권 API 토큰 발급 (별도 트랜잭션)
        initializeKisToken();
        
        // 2. 산업 데이터 생성 (별도 트랜잭션)
        createIndustries();
        
        // 3. 한국투자증권 API를 통해 KOSPI 상위 32종목과 NASDAQ 상위 30종목 업데이트 (별도 트랜잭션)
        updateStockListFromKisApi();
        
        log.info("초기 데이터 로딩이 완료되었습니다.");
    }
    
    /**
     * 한국투자증권 API 토큰을 초기화합니다.
     */
    @Transactional
    private void initializeKisToken() {
        try {
            log.info("한국투자증권 API 토큰을 발급받습니다.");
            String accessToken = tokenService.getValidAccessToken();
            log.info("한국투자증권 API 토큰 발급 완료: {}", accessToken.substring(0, 10) + "...");
        } catch (Exception e) {
            log.error("한국투자증권 API 토큰 발급에 실패했습니다: {}", e.getMessage());
            // 토큰 발급 실패 시에도 애플리케이션은 계속 실행
            log.warn("토큰 발급 실패로 인해 한국투자증권 API 연동이 제한됩니다.");
        }
    }
    
    /**
     * 한국투자증권 API를 통해 KOSPI 상위 32종목과 NASDAQ 상위 30종목을 업데이트합니다.
     */
    @Transactional
    private void updateStockListFromKisApi() {
        try {
            log.info("KOSPI 상위 32종목을 업데이트합니다.");
            stockManagementService.updateKospi200Stocks();
            log.info("KOSPI 상위 32종목 업데이트 완료");
            
            log.info("NASDAQ 상위 30종목을 업데이트합니다.");
            stockManagementService.updateNasdaqStocks();
            log.info("NASDAQ 상위 30종목 업데이트 완료");
            
        } catch (Exception e) {
            log.error("한국투자증권 API를 통한 종목 업데이트에 실패했습니다: {}", e.getMessage());
            log.warn("기본 테스트 종목 데이터를 생성합니다.");
            createTestStocks();
        }
    }
    
    @Transactional
    private void createIndustries() {
        if (industryRepository.count() > 0) {
            log.info("산업 데이터가 이미 존재합니다.");
            return;
        }
        
        List<String> industryNames = Arrays.asList(
            "전자제품", "반도체", "자동차", "화학", "금융", "건설", "통신", "의료", "식품", "기타"
        );
        
        for (String name : industryNames) {
            Industry industry = Industry.builder()
                    .name(name)
                    .build();
            industryRepository.save(industry);
        }
        
        log.info("{}개의 산업 데이터가 생성되었습니다.", industryNames.size());
    }
    
    /**
     * 한국투자증권 API 연동 실패 시 사용할 기본 테스트 종목 데이터를 생성합니다.
     */
    @Transactional
    private void createTestStocks() {
        if (stockRepository.count() > 0) {
            log.info("종목 데이터가 이미 존재합니다.");
            return;
        }
        
        // KOSPI 테스트 종목들
        List<Stock> kospiStocks = Arrays.asList(
            createStock("005930", "삼성전자", "전자제품", "KOSPI"),
            createStock("000660", "SK하이닉스", "반도체", "KOSPI"),
            createStock("035420", "NAVER", "통신", "KOSPI"),
            createStock("051910", "LG화학", "화학", "KOSPI"),
            createStock("006400", "삼성SDI", "화학", "KOSPI")
        );
        
        // NASDAQ 테스트 종목들
        List<Stock> nasdaqStocks = Arrays.asList(
            createStock("AAPL", "Apple Inc.", "전자제품", "NASDAQ"),
            createStock("MSFT", "Microsoft Corporation", "전자제품", "NASDAQ"),
            createStock("GOOGL", "Alphabet Inc.", "통신", "NASDAQ"),
            createStock("AMZN", "Amazon.com Inc.", "전자제품", "NASDAQ"),
            createStock("TSLA", "Tesla Inc.", "자동차", "NASDAQ")
        );
        
        stockRepository.saveAll(kospiStocks);
        stockRepository.saveAll(nasdaqStocks);
        
        log.info("테스트 종목 데이터가 생성되었습니다. KOSPI: {}개, NASDAQ: {}개", 
                kospiStocks.size(), nasdaqStocks.size());
    }
    
    private Stock createStock(String stockCode, String stockName, String industryName, String marketType) {
        Industry industry = industryRepository.findByName(industryName)
                .orElse(industryRepository.findByName("기타").orElse(null));
        
        return Stock.builder()
                .stockCode(stockCode)
                .stockName(stockName)
                .marketType(marketType)
                .outline("테스트용 종목")
                .industry(industry)
                .ranking(1) // 테스트용 기본 랭킹
                .build();
    }
} 