package com.hsu_mafia.motoo.api.domain.user;

import com.hsu_mafia.motoo.api.domain.portfolio.UserStock;
import com.hsu_mafia.motoo.api.domain.portfolio.UserStockRepository;
import com.hsu_mafia.motoo.api.dto.user.UserResponse;
import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import com.hsu_mafia.motoo.global.util.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;
    private final PriceUtil priceUtil;

    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // 보유 주식 총 가치 계산
        List<UserStock> userStocks = userStockRepository.findByUser(user);
        Long totalStockValue = userStocks.stream()
                .mapToLong(userStock -> {
                    Long currentPrice = priceUtil.getCurrentPrice(userStock.getStock().getId()).longValue();
                    return currentPrice * userStock.getQuantity();
                })
                .sum();

        // 총 자산 가치
        Long totalValue = user.getCash() + totalStockValue;

        // 순손익 계산
        Long netProfit = totalValue - user.getSeedMoney();

        return UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .seedMoney(user.getSeedMoney())
                .cash(user.getCash())
                .totalValue(totalValue)
                .netProfit(netProfit)
                .joinAt(user.getJoinAt())
                .build();
    }
} 