package com.hsu_mafia.motoo.api.domain.portfolio;

import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.domain.user.UserRepository;
import com.hsu_mafia.motoo.api.dto.portfolio.PortfolioDto;
import com.hsu_mafia.motoo.api.dto.portfolio.PortfolioStockDto;
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
public class PortfolioService {
    private final UserStockRepository userStockRepository;
    private final UserRepository userRepository;
    private final PriceUtil priceUtil;

    public PortfolioDto getPortfolio(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        List<UserStock> userStocks = userStockRepository.findByUser(user);
        
        List<PortfolioStockDto> portfolioStocks = userStocks.stream()
                .map(this::convertToPortfolioStockDto)
                .collect(Collectors.toList());

        // 총 주식 가치 계산
        Long totalStockValue = portfolioStocks.stream()
                .mapToLong(PortfolioStockDto::getCurrentValue)
                .sum();

        // 총 자산 가치
        Long totalValue = user.getCash() + totalStockValue;

        // 순손익 계산
        Long netProfit = totalValue - user.getSeedMoney();

        return PortfolioDto.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .seedMoney(user.getSeedMoney())
                .cash(user.getCash())
                .totalStockValue(totalStockValue)
                .totalValue(totalValue)
                .netProfit(netProfit)
                .stocks(portfolioStocks)
                .build();
    }

    private PortfolioStockDto convertToPortfolioStockDto(UserStock userStock) {
        Stock stock = userStock.getStock();
        Long currentPrice = priceUtil.getCurrentPrice(stock.getId()).longValue();
        Long currentValue = currentPrice * userStock.getQuantity();
        Long profitLoss = currentValue - (userStock.getAverageBuyPrice() * userStock.getQuantity());
        
        // 수익률 계산 (소수점 2자리까지)
        double profitLossRate = userStock.getAverageBuyPrice() > 0 
                ? ((double) (currentPrice - userStock.getAverageBuyPrice()) / userStock.getAverageBuyPrice()) * 100
                : 0.0;

        return PortfolioStockDto.builder()
                .stockId(stock.getId())
                .stockName(stock.getStockName())
                .quantity(userStock.getQuantity())
                .averageBuyPrice(userStock.getAverageBuyPrice())
                .currentPrice(currentPrice)
                .currentValue(currentValue)
                .profitLoss(profitLoss)
                .profitLossRate(Math.round(profitLossRate * 100.0) / 100.0)
                .build();
    }
} 