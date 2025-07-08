package com.hsu_mafia.motoo.api.presentation.portfolio;

import com.hsu_mafia.motoo.api.domain.portfolio.PortfolioService;
import com.hsu_mafia.motoo.api.dto.portfolio.PortfolioDto;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
public class PortfolioController {
    private final PortfolioService portfolioService;

    @GetMapping
    public CommonResponse<PortfolioDto> getPortfolio() {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경
        PortfolioDto portfolio = portfolioService.getPortfolio(userId);
        return CommonResponse.success(portfolio);
    }
} 