package com.hsu_mafia.motoo.api.domain.order;

import com.hsu_mafia.motoo.api.dto.order.OrderResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderMapper INSTANCE = Mappers.getMapper(OrderMapper.class);

    @Mapping(target = "orderId", source = "id")
    @Mapping(target = "stockId", source = "stock.id")
    @Mapping(target = "stockName", source = "stock.stockName")
    OrderResponse toOrderResponse(Order order);
} 