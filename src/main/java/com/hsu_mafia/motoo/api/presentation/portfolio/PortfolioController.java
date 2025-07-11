package com.hsu_mafia.motoo.api.presentation.portfolio;

import com.hsu_mafia.motoo.api.domain.portfolio.PortfolioService;
import com.hsu_mafia.motoo.api.dto.portfolio.PortfolioDto;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    @GetMapping
    @Operation(
    summary = "포트폴리오 조회",
    description = "사용자의 전체 자산, 보유 주식, 수익률 등 포트폴리오 정보를 조회합니다."
)
    public CommonResponse<PortfolioDto> getPortfolio() {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경
        PortfolioDto portfolio = portfolioService.getPortfolio(userId);
        return CommonResponse.success(portfolio);
    }
} 