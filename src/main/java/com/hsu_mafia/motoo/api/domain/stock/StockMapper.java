package com.hsu_mafia.motoo.api.domain.stock;

import com.hsu_mafia.motoo.api.dto.stock.StockResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring")
public interface StockMapper {
    
    @Mapping(source = "stockCode", target = "stockId")
    @Mapping(source = "industry.name", target = "industryName")
    @Mapping(target = "currentPrice", ignore = true)
    @Mapping(target = "priceDifference", ignore = true)
    @Mapping(target = "rateDifference", ignore = true)
    @Mapping(target = "tradingVolume", ignore = true)
    @Mapping(target = "min52", ignore = true)
    @Mapping(target = "max52", ignore = true)
    @Mapping(target = "per", ignore = true)
    @Mapping(target = "pbr", ignore = true)
    StockResponse toStockResponse(Stock stock);
    
    List<StockResponse> toStockResponseList(List<Stock> stocks);
    
    @Named("stockCodeToStockId")
    default String stockCodeToStockId(String stockCode) {
        return stockCode;
    }
} 