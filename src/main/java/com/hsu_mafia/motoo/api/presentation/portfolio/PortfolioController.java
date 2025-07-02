package com.hsu_mafia.motoo.api.presentation.portfolio;

import com.hsu_mafia.motoo.api.dto.portfolio.PortfolioDto;
import com.hsu_mafia.motoo.api.domain.portfolio.PortfolioService;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
@Tag(name = "Portfolio", description = "포트폴리오 관련 API")
public class PortfolioController {
    private final PortfolioService portfolioService;

    @GetMapping
    @Operation(summary = "내 포트폴리오 조회", description = "사용자의 포트폴리오를 조회합니다.")
    public CommonResponse<PortfolioDto> getPortfolio() {
        Long userId = 1L;
        return CommonResponse.success(portfolioService.getPortfolio(userId));
    }
} 