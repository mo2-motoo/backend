package com.hsu_mafia.motoo.api.domain.portfolio;

import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.dto.portfolio.PortfolioDto;
import com.hsu_mafia.motoo.api.dto.portfolio.PortfolioStockDto;
import com.hsu_mafia.motoo.api.dto.user.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PortfolioMapper {
    
    @Mapping(source = "id", target = "userId")
    @Mapping(source = "stocks", target = "stocks")
    @Mapping(target = "totalStockValue", ignore = true)
    @Mapping(target = "totalValue", ignore = true)
    @Mapping(target = "netProfit", ignore = true)
    PortfolioDto toPortfolioDto(User user);
    
    @Mapping(source = "stock.stockCode", target = "stockId")
    @Mapping(source = "stock.stockName", target = "stockName")
    @Mapping(target = "currentPrice", ignore = true)
    @Mapping(target = "currentValue", ignore = true)
    @Mapping(target = "profitLoss", ignore = true)
    @Mapping(target = "profitLossRate", ignore = true)
    PortfolioStockDto toPortfolioStockDto(UserStock userStock);
    
    List<PortfolioStockDto> toPortfolioStockDtoList(List<UserStock> userStocks);
    
    @Mapping(source = "id", target = "userId")
    @Mapping(target = "totalValue", ignore = true)
    @Mapping(target = "netProfit", ignore = true)
    UserResponse toUserResponse(User user);
} 