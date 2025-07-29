package com.hsu_mafia.motoo.api.presentation.user;

import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.domain.user.UserService;
import com.hsu_mafia.motoo.api.domain.portfolio.UserStock;
import com.hsu_mafia.motoo.api.dto.user.UserResponse;
import com.hsu_mafia.motoo.api.domain.portfolio.PortfolioMapper;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import com.hsu_mafia.motoo.global.util.PriceUtil;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final PortfolioMapper portfolioMapper;
    private final PriceUtil priceUtil;

    @GetMapping("/profile")
    @Operation(
        summary = "사용자 프로필 조회",
        description = "사용자의 기본 정보, 자산, 가입일 등 프로필 정보를 조회합니다."
    )
    public ResponseEntity<CommonResponse<UserResponse>> getProfile() {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경
        User user = userService.getProfile(userId);
        
        // 보유 주식 총 가치 계산
        List<UserStock> userStocks = user.getStocks();
        Long totalStockValue = userStocks.stream()
                .mapToLong(userStock -> {
                    Long currentPrice = priceUtil.getCurrentPrice(userStock.getStock().getStockCode()).longValue();
                    return currentPrice * userStock.getQuantity();
                })
                .sum();

        // 총 자산 가치
        Long totalValue = user.getCash() + totalStockValue;

        // 순손익 계산
        Long netProfit = totalValue - user.getSeedMoney();

        // UserResponse를 Builder 패턴으로 생성
        UserResponse profile = UserResponse.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .cash(user.getCash())
                .seedMoney(user.getSeedMoney())
                .totalValue(totalValue)
                .netProfit(netProfit)
                .build();
                
        return ResponseEntity.status(HttpStatus.OK)
                .body(CommonResponse.success(profile));
    }
} 