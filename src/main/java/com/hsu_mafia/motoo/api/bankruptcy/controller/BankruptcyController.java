package com.hsu_mafia.motoo.api.bankruptcy.controller;

import com.hsu_mafia.motoo.api.bankruptcy.dto.response.AllBankruptcyRes;
import com.hsu_mafia.motoo.api.bankruptcy.dto.response.DetailBankruptcyRes;
import com.hsu_mafia.motoo.api.bankruptcy.entity.BankruptcyEntity;
import com.hsu_mafia.motoo.api.bankruptcy.mapper.BankruptcyMapper;
import com.hsu_mafia.motoo.api.bankruptcy.service.BankruptcyService;
import com.hsu_mafia.motoo.api.stock.entity.ExecutionEntity;
import com.hsu_mafia.motoo.api.stock.mapper.StockMapper;
import com.hsu_mafia.motoo.api.stock.response.ExecutionRes;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("bankruptcies")
public class BankruptcyController {

    private final BankruptcyService bankruptcyService;

    // 파산 가능 여부 조회
    @GetMapping("/available")
    public CommonResponse<Boolean> isBankruptcyAvailable() {
        Long userId = 1L;
        return CommonResponse.success(bankruptcyService.isBankruptcyAvailable(userId));
    }

    // 파산 진행 (파산 엔티티 반환)
    @PostMapping
    public CommonResponse<DetailBankruptcyRes> proceedBankrupt() {
        Long userId = 1L;
        BankruptcyEntity entity = bankruptcyService.proceedBankrupt(userId);
        DetailBankruptcyRes detailBankruptcyRes = BankruptcyMapper.INSTANCE.toDetailBankruptcyRes(entity);
        return CommonResponse.success(detailBankruptcyRes);
    }

    // 전체 파산 기록 조회
    @GetMapping
    public CommonResponse<List<AllBankruptcyRes>> getAllBankruptcy() {
        Long userId = 1L;
        List<BankruptcyEntity> entities = bankruptcyService.getAllBankruptcy(userId);
        List<AllBankruptcyRes> allBankruptcyResList = entities.stream()
                .map(BankruptcyMapper.INSTANCE::toAllBankruptcyRes)
                .collect(Collectors.toList());
        return CommonResponse.success(allBankruptcyResList);
    }

    // 특정 파산 기록 상세 조회
    @GetMapping("/{bankruptcyNo}")
    public CommonResponse<DetailBankruptcyRes> getDetailBankruptcy(@PathVariable Integer bankruptcyNo) {
        Long userId = 1L;
        BankruptcyEntity entity = bankruptcyService.getDetailBankruptcy(userId, bankruptcyNo);
        DetailBankruptcyRes detailBankruptcyRes = BankruptcyMapper.INSTANCE.toDetailBankruptcyRes(entity);
        return CommonResponse.success(detailBankruptcyRes);
    }

    // 특정 파산 시점의 거래내역 조회
    @GetMapping("/{bankruptcyNo}/executions")
    public CommonResponse<List<ExecutionRes>> getAllExecutionByBankruptcy(@PathVariable Integer bankruptcyNo) {
        Long userId = 1L;
        List<ExecutionEntity> entities = bankruptcyService.getAllExecutionByBankruptcy(userId, bankruptcyNo);
        List<ExecutionRes> executionResList = entities.stream()
                .map(e -> StockMapper.INSTANCE.toExecutionRes(e, (long) e.getPrice() * e.getAmount()))
                .collect(Collectors.toList());
        return CommonResponse.success(executionResList);
    }
} 