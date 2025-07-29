package com.hsu_mafia.motoo.api.domain.execution;

import com.hsu_mafia.motoo.api.dto.execution.ExecutionDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ExecutionMapper {
    ExecutionMapper INSTANCE = Mappers.getMapper(ExecutionMapper.class);

    @Mapping(target = "executionId", source = "id")
    @Mapping(target = "stockId", source = "stock.stockCode")
    @Mapping(target = "stockName", source = "stock.stockName")
    ExecutionDto toExecutionDto(Execution execution);
} 