package com.hsu_mafia.motoo.api.presentation.portfolio;

import com.hsu_mafia.motoo.api.domain.portfolio.PortfolioService;
import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.domain.portfolio.UserStock;
import com.hsu_mafia.motoo.api.dto.portfolio.PortfolioDto;
import com.hsu_mafia.motoo.api.dto.portfolio.PortfolioStockDto;
import com.hsu_mafia.motoo.api.domain.portfolio.PortfolioMapper;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import com.hsu_mafia.motoo.global.util.PriceUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;
    private final PortfolioMapper portfolioMapper;
    private final PriceUtil priceUtil;

    @GetMapping
    @Operation(
    summary = "포트폴리오 조회",
    description = "사용자의 전체 자산, 보유 주식, 수익률 등 포트폴리오 정보를 조회합니다.")
    public ResponseEntity<CommonResponse<PortfolioDto>> getPortfolio(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경
        User user = portfolioService.getPortfolio(userId);
        
        // UserStock 정보를 페이지네이션으로 가져와서 PortfolioDto로 변환
        Pageable pageable = PageRequest.of(page, size);
        Slice<UserStock> userStocks = portfolioService.getUserStocks(userId, pageable);
                List<PortfolioStockDto> portfolioStocks = portfolioMapper.toPortfolioStockDtoList(userStocks.getContent());
        
        // PriceUtil을 통해 현재가 정보를 포함한 PortfolioStockDto 리스트 생성
        List<PortfolioStockDto> enrichedPortfolioStocks = portfolioStocks.stream()
                .map(this::createEnrichedPortfolioStockDto)
                .collect(Collectors.toList());
        
        // 총 주식 가치 계산
        Long totalStockValue = enrichedPortfolioStocks.stream()
                .mapToLong(PortfolioStockDto::getCurrentValue)
                .sum();

        // 총 자산 가치
        Long totalValue = user.getCash() + totalStockValue;

        // 순손익 계산
        Long netProfit = totalValue - user.getSeedMoney();

        // PortfolioDto를 Builder 패턴으로 생성
        PortfolioDto portfolio = PortfolioDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .cash(user.getCash())
                .seedMoney(user.getSeedMoney())
                .totalStockValue(totalStockValue)
                .totalValue(totalValue)
                .netProfit(netProfit)
                .stocks(enrichedPortfolioStocks)
                .build();
                
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(portfolio));
    }
    
    /**
     * PortfolioStockDto에 PriceUtil 정보를 추가하여 새로운 객체를 생성하는 헬퍼 메서드
     */
    private PortfolioStockDto createEnrichedPortfolioStockDto(PortfolioStockDto dto) {
        String stockCode = dto.getStockId();
        Long currentPrice = priceUtil.getCurrentPrice(stockCode).longValue();
        Long currentValue = currentPrice * dto.getQuantity();
        Long profitLoss = currentValue - (dto.getAverageBuyPrice() * dto.getQuantity());
        
        // 수익률 계산 (소수점 2자리까지)
        double profitLossRate = dto.getAverageBuyPrice() > 0 
                ? ((double) (currentPrice - dto.getAverageBuyPrice()) / dto.getAverageBuyPrice()) * 100
                : 0.0;

        return PortfolioStockDto.builder()
                .stockId(dto.getStockId())
                .stockName(dto.getStockName())
                .quantity(dto.getQuantity())
                .averageBuyPrice(dto.getAverageBuyPrice())
                .currentPrice(currentPrice)
                .currentValue(currentValue)
                .profitLoss(profitLoss)
                .profitLossRate(Math.round(profitLossRate * 100.0) / 100.0)
                .build();
    }
} 