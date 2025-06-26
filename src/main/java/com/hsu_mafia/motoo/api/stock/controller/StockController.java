package com.hsu_mafia.motoo.api.stock.controller;

import com.hsu_mafia.motoo.api.stock.entity.StockEntity;
import com.hsu_mafia.motoo.api.stock.entity.UserStockEntity;
import com.hsu_mafia.motoo.api.stock.entity.ExecutionEntity;
import com.hsu_mafia.motoo.api.stock.mapper.StockMapper;

import com.hsu_mafia.motoo.api.stock.service.StockService;
import com.hsu_mafia.motoo.api.stock.service.ExecutionService;
import com.hsu_mafia.motoo.api.stock.service.UserStockService;
import com.hsu_mafia.motoo.api.interest.entity.InterestEntity;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import com.hsu_mafia.motoo.api.stock.dto.request.StockBuyRequest;
import com.hsu_mafia.motoo.api.stock.dto.request.StockSellRequest;
import com.hsu_mafia.motoo.api.stock.dto.response.AllStockRes;
import com.hsu_mafia.motoo.api.stock.dto.response.SpecificStockRes;
import com.hsu_mafia.motoo.api.stock.dto.response.MyStockRes;
import com.hsu_mafia.motoo.api.stock.dto.response.ExecutionRes;
import com.hsu_mafia.motoo.api.stock.dto.response.InterestRes;
import com.hsu_mafia.motoo.api.stock.dto.response.StockRankRes;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.stream.Collectors;
import com.hsu_mafia.motoo.global.util.PriceUtil;

@RequiredArgsConstructor
@RestController
@RequestMapping("/stocks")
public class StockController {
    private final StockService stockService;
    private final ExecutionService executionService;
    private final UserStockService userStockService;
    private final PriceUtil priceUtil;

    // 전체 주식 정보 조회
    @GetMapping
    public CommonResponse<List<AllStockRes>> getAllStocks() {
        List<StockEntity> stocks = stockService.findAllStocks();
        List<AllStockRes> allStockResList = stocks.stream()
                .map(stock -> StockMapper.INSTANCE.toAllStockRes(
                        stock,
                        priceUtil.getCurrentPrice(stock.getId()),
                        priceUtil.getPriceDifference(stock.getId()),
                        priceUtil.getRateDifference(stock.getId()),
                        null // liked 정보는 관심종목 여부에 따라 별도 처리 필요
                ))
                .collect(Collectors.toList());
        return CommonResponse.success(allStockResList);
    }

    // 단일 주식 상세 조회
    @GetMapping("/{stockId}")
    public CommonResponse<SpecificStockRes> getStock(@PathVariable String stockId) {
        StockEntity stock = stockService.getStockInfo(stockId);
        SpecificStockRes specificStockRes = StockMapper.INSTANCE.toSpecificStockRes(
                stock,
                priceUtil.getMinCandle(stockId),
                priceUtil.getDayCandle(stockId),
                priceUtil.getWeekCandle(stockId),
                priceUtil.getTotalPrice(stockId),
                priceUtil.getCurrentPrice(stockId),
                priceUtil.getMin52(stockId),
                priceUtil.getMax52(stockId),
                priceUtil.getPer(stockId),
                priceUtil.getPbr(stockId),
                priceUtil.getPriceDifference(stockId) != null ? Integer.valueOf(priceUtil.getPriceDifference(stockId)) : null,
                priceUtil.getRateDifference(stockId) != null ? Double.valueOf(priceUtil.getRateDifference(stockId)) : null,
                null, // interested
                null, // hold
                stock.getOutline(),
                stock.getImageUrl(),
                stock.getTotalStock(),
                stock.getIndustry() != null ? stock.getIndustry().getIndustryClass() : null,
                stock.getIndustry() != null ? stock.getIndustry().getWics() : null
        );
        return CommonResponse.success(specificStockRes);
    }

    // 주식 매수
    @PostMapping("/{stockId}/buy")
    public CommonResponse<Integer> buyStock(@PathVariable String stockId, @RequestBody StockBuyRequest request) {
        Long userId = 1L;
        Integer bought = stockService.buyStock(userId, stockId, request.getCount());
        return CommonResponse.success(bought);
    }

    // 주식 매도
    @PostMapping("/{stockId}/sell")
    public CommonResponse<Integer> sellStock(@PathVariable String stockId, @RequestBody StockSellRequest request) {
        Long userId = 1L;
        Integer sold = stockService.sellStock(userId, stockId, request.getCount());
        return CommonResponse.success(sold);
    }

    // 내 보유 주식 전체 조회
    @GetMapping("/my")
    public CommonResponse<List<MyStockRes>> getMyStocks() {
        Long userId = 1L;
        List<UserStockEntity> userStocks = userStockService.myStocks(userId);
        List<MyStockRes> myStockResList = userStocks.stream()
                .map(us -> StockMapper.INSTANCE.toMyStockRes(
                        us.getStock(),
                        us.getHold(),
                        us.getAveragePrice(),
                        priceUtil.getCurrentPrice(us.getStock().getId()),
                        (long) priceUtil.getCurrentPrice(us.getStock().getId()) * us.getHold(),
                        ((long) priceUtil.getCurrentPrice(us.getStock().getId()) - us.getAveragePrice()) * us.getHold(),
                        us.getAveragePrice() != 0 ? ((double) (priceUtil.getCurrentPrice(us.getStock().getId()) - us.getAveragePrice()) / us.getAveragePrice()) * 100 : null
                ))
                .collect(Collectors.toList());
        return CommonResponse.success(myStockResList);
    }

    // 내 보유 특정 주식 조회
    @GetMapping("/my/{stockId}")
    public CommonResponse<MyStockRes> getMyStock(@PathVariable String stockId) {
        Long userId = 1L;
        UserStockEntity userStock = userStockService.myStock(userId, stockId);
        MyStockRes myStockRes = StockMapper.INSTANCE.toMyStockRes(
                userStock.getStock(),
                userStock.getHold(),
                userStock.getAveragePrice(),
                priceUtil.getCurrentPrice(stockId),
                (long) priceUtil.getCurrentPrice(stockId) * userStock.getHold(),
                ((long) priceUtil.getCurrentPrice(stockId) - userStock.getAveragePrice()) * userStock.getHold(),
                userStock.getAveragePrice() != 0 ? ((double) (priceUtil.getCurrentPrice(stockId) - userStock.getAveragePrice()) / userStock.getAveragePrice()) * 100 : null
        );
        return CommonResponse.success(myStockRes);
    }

    // 내 전체 거래내역
    @GetMapping("/executions")
    public CommonResponse<List<ExecutionRes>> getMyExecutions() {
        Long userId = 1L;
        List<ExecutionEntity> executions = executionService.myAllExecution(userId);
        List<ExecutionRes> executionResList = executions.stream()
                .map(e -> StockMapper.INSTANCE.toExecutionRes(e, (long) e.getPrice() * e.getAmount()))
                .collect(Collectors.toList());
        return CommonResponse.success(executionResList);
    }

    // 내 특정 주식 거래내역
    @GetMapping("/executions/{stockId}")
    public CommonResponse<List<ExecutionRes>> getMyExecutionsByStock(@PathVariable String stockId) {
        Long userId = 1L;
        List<ExecutionEntity> executions = executionService.myExecution(userId, stockId);
        List<ExecutionRes> executionResList = executions.stream()
                .map(e -> StockMapper.INSTANCE.toExecutionRes(e, (long) e.getPrice() * e.getAmount()))
                .collect(Collectors.toList());
        return CommonResponse.success(executionResList);
    }

    // 관심종목 추가
    @PostMapping("/{stockId}/interest")
    public CommonResponse<Boolean> addInterest(@PathVariable String stockId) {
        Long userId = 1L;
        Boolean isAdded = stockService.addInterest(userId, stockId);
        return CommonResponse.success(isAdded);
    }

    // 관심종목 취소
    @DeleteMapping("/{stockId}/interest")
    public CommonResponse<Boolean> cancelInterest(@PathVariable String stockId) {
        Long userId = 1L;
        Boolean isCanceled = stockService.cancelInterest(userId, stockId);
        return CommonResponse.success(isCanceled);
    }

    // 관심종목 전체 조회
    @GetMapping("/interests")
    public CommonResponse<List<InterestRes>> getInterests() {
        Long userId = 1L;
        List<InterestEntity> interests = stockService.showInterest(userId);
        List<InterestRes> interestResList = interests.stream()
                .map(interest -> StockMapper.INSTANCE.toInterestRes(
                        interest.getStock(),
                        priceUtil.getCurrentPrice(interest.getStock().getId()),
                        priceUtil.getPriceDifference(interest.getStock().getId()) != null ? Integer.valueOf(priceUtil.getPriceDifference(interest.getStock().getId())) : null,
                        priceUtil.getRateDifference(interest.getStock().getId()) != null ? Double.valueOf(priceUtil.getRateDifference(interest.getStock().getId())) : null,
                        interest.getIsInterested()
                ))
                .collect(Collectors.toList());
        return CommonResponse.success(interestResList);
    }

    // 주식 랭킹
    @GetMapping("/rank")
    public CommonResponse<List<StockRankRes>> getStockRank() {
        List<StockEntity> stocks = stockService.rankByVolume();
        List<StockRankRes> stockRankResList = stocks.stream()
                .map(stock -> StockMapper.INSTANCE.toRankRes(
                        null,
                        stock,
                        priceUtil.getCurrentPrice(stock.getId()),
                        priceUtil.getRateDifference(stock.getId())
                ))
                .collect(Collectors.toList());
        return CommonResponse.success(stockRankResList);
    }

    // 내 평가액
    @GetMapping("/value")
    public CommonResponse<Long> getMyValue() {
        Long userId = 1L;
        Long value = stockService.calculateValue(userId);
        return CommonResponse.success(value);
    }
}
