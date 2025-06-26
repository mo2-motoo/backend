package com.hsu_mafia.motoo.api.stock.mapper;

import com.hsu_mafia.motoo.api.interest.entity.InterestEntity;
import com.hsu_mafia.motoo.api.stock.entity.ExecutionEntity;
import com.hsu_mafia.motoo.api.stock.entity.StockEntity;
import com.hsu_mafia.motoo.api.stock.entity.UserStockEntity;
import com.hsu_mafia.motoo.api.stock.dto.response.StockRankRes;
import com.hsu_mafia.motoo.api.stock.dto.response.StockVolumeRes;
import com.hsu_mafia.motoo.api.stock.dto.response.UserValueRes;
import com.hsu_mafia.motoo.api.stock.dto.response.AllStockRes;
import com.hsu_mafia.motoo.api.stock.dto.response.ExecutionRes;
import com.hsu_mafia.motoo.api.stock.dto.response.InterestRes;
import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import com.hsu_mafia.motoo.global.exception.MapperException;
import com.hsu_mafia.motoo.api.stock.dto.response.SpecificStockRes;
import com.hsu_mafia.motoo.api.stock.dto.response.MinuteRes;
import com.hsu_mafia.motoo.api.stock.dto.response.DayWeekRes;
import com.hsu_mafia.motoo.api.stock.dto.response.MyStockRes;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface StockMapper {

    StockMapper INSTANCE = Mappers.getMapper(StockMapper.class);

    @Mapping(source = "count", target = "hold")
    @Mapping(source = "price", target = "averagePrice")
    @Mapping(source = "user", target = "user")
    UserStockEntity toUserStock(UserEntity user, StockEntity stock, Integer count, Integer price)
        throws MapperException;

    @Mapping(source = "count", target = "amount")
    @Mapping(source = "user", target = "user")
    ExecutionEntity toExecution(StockEntity stock, UserEntity user, Integer price, Integer count, Boolean isBought)
        throws MapperException;

    @Mapping(source = "stock.id", target = "stockId")
    AllStockRes toAllStockRes(StockEntity stock, Integer currentPrice, String priceDifference,
        String rateDifference, Boolean liked) throws MapperException;

    UserValueRes toUserValueRes(UserEntity user, Long totalValue) throws MapperException;

    StockVolumeRes toStockVolumeRes(StockEntity stock, Long volume) throws MapperException;

    @Mapping(source = "stock.id", target = "stockId")
    StockRankRes toRankRes(Integer rank, StockEntity stock, Integer price, String rateDifference)
        throws MapperException;

    @Mapping(source = "stock.id", target = "stockId")
    InterestRes toInterestRes(StockEntity stock, Integer currentPrice, Integer priceDifference,
        Double rateDifference, Boolean isInterested) throws MapperException;

    @Mapping(source = "stock.industry.industryClass", target = "industryClass")
    @Mapping(source = "stock.industry.wics", target = "wics")
    SpecificStockRes toSpecificStockRes(StockEntity stock, List<MinuteRes> minCandle,
        List<DayWeekRes> dayCandle, List<DayWeekRes> weekCandle, String totalPrice,
        Integer currentPrice, Integer min52, Integer max52, String per, String pbr,
        Integer priceDifference, Double rateDifference, Boolean interested, Integer hold)
        throws MapperException;

    @Mapping(source = "stock.id", target = "stockId")
    MyStockRes toMyStockRes(StockEntity stock, Integer hold, Integer averagePrice, Integer currentPrice,
        Long totalPrice, Long profit, Double profitRate) throws MapperException;

    @Mapping(source = "execution.stock.id", target = "stockId")
    @Mapping(source = "execution.stock.stockName", target = "stockName")
    ExecutionRes toExecutionRes(ExecutionEntity execution, Long totalPrice) throws MapperException;

    InterestEntity toInterest(UserEntity user, StockEntity stock, Boolean isInterested) throws MapperException;

    MinuteRes toMinuteRes(String time, String price, String amount) throws MapperException;

    DayWeekRes toDayWeekRes(String date, String startPrice, String endPrice, String bestPrice,
        String worstPrice, String amount) throws MapperException;
}
