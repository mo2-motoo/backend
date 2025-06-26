package com.hsu_mafia.motoo.api.bankruptcy.service;

import com.hsu_mafia.motoo.api.bankruptcy.entity.BankruptcyEntity;
import com.hsu_mafia.motoo.api.bankruptcy.repository.BankruptcyRepository;
import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import com.hsu_mafia.motoo.api.user.repository.UserRepository;
import com.hsu_mafia.motoo.api.stock.entity.ExecutionEntity;
import com.hsu_mafia.motoo.api.stock.entity.UserStockEntity;
import com.hsu_mafia.motoo.api.stock.repository.ExecutionRepository;
import com.hsu_mafia.motoo.api.stock.repository.UserStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BankruptcyService {
    private final BankruptcyRepository bankruptcyRepository;
    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;
    private final ExecutionRepository executionRepository;
    private final Double RATIO = 70D;

    public Boolean isBankruptcyAvailable(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Long stockValue = calculateValue(user);
        Long totalAsset = stockValue + user.getCash();
        Long seedMoney = user.getSeedMoney();
        return (100L * totalAsset / seedMoney < RATIO);
    }

    public BankruptcyEntity proceedBankrupt(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Integer bankruptcyNo = user.getBankruptcyNo();
        Long lastCash = user.getCash();
        Long lastSeedMoney = user.getSeedMoney();
        Long lastTotalAsset = calculateValue(user) + lastCash;
        Long netIncome = lastTotalAsset - lastSeedMoney;
        Double roi = (lastSeedMoney != 0) ? (double) netIncome / lastSeedMoney * 100 : 0.0;
        BankruptcyEntity bankruptcy = BankruptcyEntity.builder()
                .user(user)
                .bankruptcyNo(bankruptcyNo)
                .lastCash(lastCash)
                .lastSeedMoney(lastSeedMoney)
                .lastTotalAsset(lastTotalAsset)
                .netIncome(netIncome)
                .roi(roi)
                .bankruptAt(LocalDateTime.now())
                .build();
        bankruptcyRepository.save(bankruptcy);
        user.resetAfterBankruptcy();
        userRepository.save(user);
        return bankruptcy;
    }

    public List<BankruptcyEntity> getAllBankruptcy(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<BankruptcyEntity> bankruptcyList = bankruptcyRepository.findAllByUser(user);
        if (bankruptcyList.isEmpty()) {
            throw new RuntimeException("No bankruptcy data");
        }
        return bankruptcyList;
    }

    public BankruptcyEntity getDetailBankruptcy(Long userId, Integer bankruptcyNo) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        BankruptcyEntity bankruptcy = bankruptcyRepository.findByUserAndBankruptcyNo(user, bankruptcyNo);
        if (bankruptcy == null) {
            throw new RuntimeException("No bankruptcy data");
        }
        return bankruptcy;
    }

    public List<ExecutionEntity> getAllExecutionByBankruptcy(Long userId, Integer bankruptcyNo) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<ExecutionEntity> executionList = executionRepository.findAllByUserAndBankruptcyNo(user, bankruptcyNo);
        if (executionList.isEmpty()) {
            throw new RuntimeException("No execution data");
        }
        executionList.sort(Comparator.comparing(ExecutionEntity::getDealAt).reversed());
        return executionList;
    }

    public Long calculateValue(UserEntity user) {
        Long stockValue = 0L;
        List<UserStockEntity> userStockList = userStockRepository.findAllByUserAndBankruptcyNo(user, user.getBankruptcyNo());
        if (!userStockList.isEmpty()) {
            for (UserStockEntity userStock : userStockList) {
                stockValue += (long) userStock.getAveragePrice() * userStock.getHold();
            }
        }
        return stockValue;
    }
} 