package com.hsu_mafia.motoo.api.stock.service;

import com.hsu_mafia.motoo.api.stock.entity.StockEntity;
import com.hsu_mafia.motoo.api.stock.entity.UserStockEntity;
import com.hsu_mafia.motoo.api.stock.entity.ExecutionEntity;
import com.hsu_mafia.motoo.api.stock.repository.StockRepository;
import com.hsu_mafia.motoo.api.stock.repository.UserStockRepository;
import com.hsu_mafia.motoo.api.stock.repository.ExecutionRepository;
import com.hsu_mafia.motoo.api.interest.entity.InterestEntity;
import com.hsu_mafia.motoo.api.interest.repository.InterestRepository;
import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import com.hsu_mafia.motoo.api.user.repository.UserRepository;
import com.hsu_mafia.motoo.global.util.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;
    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;
    private final InterestRepository interestRepository;
    private final ExecutionRepository executionRepository;
    private final PriceUtil priceUtil;

    public List<StockEntity> findAllStocks() {
        return stockRepository.findAll();
    }

    public StockEntity findStockById(String stockId) {
        return stockRepository.findById(stockId).orElseThrow(() -> new RuntimeException("Stock not found"));
    }

    @Transactional
    public Integer buyStock(Long userId, String stockId, Integer count) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        StockEntity stock = stockRepository.findById(stockId).orElseThrow(() -> new RuntimeException("Stock not found"));
        if (count == null || count <= 0) return 0;
        Integer price = priceUtil.getCurrentPrice(stockId);
        Long totalPrice = (long) price * count;
        Long cash = user.getCash();
        if (cash < totalPrice) {
            count = (int) (cash / price);
            if (count == 0) return 0;
            totalPrice = (long) price * count;
        }
        UserStockEntity userStock = userStockRepository.findAllByUserAndBankruptcyNo(user, user.getBankruptcyNo())
                .stream().filter(us -> us.getStock().getId().equals(stockId)).findFirst().orElse(null);
        if (userStock == null) {
            userStock = UserStockEntity.builder()
                    .user(user)
                    .stock(stock)
                    .hold(count)
                    .averagePrice(price)
                    .bankruptcyNo(user.getBankruptcyNo())
                    .build();
            userStockRepository.save(userStock);
        } else {
            int hold = userStock.getHold() + count;
            int averagePrice = (int) (((long) userStock.getAveragePrice() * userStock.getHold() + totalPrice) / hold);
            userStock = UserStockEntity.builder()
                    .id(userStock.getId())
                    .user(user)
                    .stock(stock)
                    .hold(hold)
                    .averagePrice(averagePrice)
                    .bankruptcyNo(user.getBankruptcyNo())
                    .build();
            userStockRepository.save(userStock);
        }
        user = user.toBuilder().cash(cash - totalPrice).build();
        userRepository.save(user);
        ExecutionEntity execution = ExecutionEntity.builder()
                .user(user)
                .stock(stock)
                .price(price.longValue())
                .amount(count)
                .isBought(true)
                .dealAt(java.time.LocalDateTime.now())
                .bankruptcyNo(user.getBankruptcyNo())
                .build();
        executionRepository.save(execution);
        return count;
    }

    public Long calculateValue(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Long stockValue = 0L;
        List<UserStockEntity> userStockList = userStockRepository.findAllByUserAndBankruptcyNo(user, user.getBankruptcyNo());
        if (!userStockList.isEmpty()) {
            for (UserStockEntity userStock : userStockList) {
                Integer currentPrice = priceUtil.getCurrentPrice(userStock.getStock().getId());
                Integer hold = userStock.getHold();
                hold = Integer.max(0, hold);
                Long calculatedValue = (long) currentPrice * hold;
                stockValue += calculatedValue;
            }
        }
        Long cash = user.getCash();
        return cash + stockValue;
    }

    public List<StockEntity> rankByVolume() {
        // 거래량 상위 10개 종목 조회 (예시: 전체 종목 반환)
        return stockRepository.findAll();
    }

    public List<InterestEntity> showInterest(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return interestRepository.findAllByUser(user);
    }

    public StockEntity getStockInfo(String stockId) {
        return stockRepository.findById(stockId).orElseThrow(() -> new RuntimeException("Stock not found"));
    }

    public List<UserStockEntity> myStocks(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return userStockRepository.findAllByUserAndBankruptcyNo(user, user.getBankruptcyNo());
    }

    public List<ExecutionEntity> myAllExecution(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return executionRepository.findAllByUserAndBankruptcyNo(user, user.getBankruptcyNo());
    }

    public UserStockEntity myStock(Long userId, String stockId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return userStockRepository.findAllByUserAndBankruptcyNo(user, user.getBankruptcyNo())
                .stream().filter(us -> us.getStock().getId().equals(stockId)).findFirst()
                .orElseThrow(() -> new RuntimeException("UserStock not found"));
    }

    public List<ExecutionEntity> myExecution(Long userId, String stockId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return executionRepository.findAllByUserAndBankruptcyNo(user, user.getBankruptcyNo())
                .stream().filter(e -> e.getStock().getId().equals(stockId)).toList();
    }

    @Transactional
    public Integer sellStock(Long userId, String stockId, Integer count) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        StockEntity stock = stockRepository.findById(stockId).orElseThrow(() -> new RuntimeException("Stock not found"));
        UserStockEntity userStock = userStockRepository.findAllByUserAndBankruptcyNo(user, user.getBankruptcyNo())
                .stream().filter(us -> us.getStock().getId().equals(stockId)).findFirst().orElse(null);
        if (userStock == null || userStock.getHold() < count) return 0;
        Integer price = priceUtil.getCurrentPrice(stockId);
        Long totalPrice = (long) price * count * 99685 / 100000; // 수수료 반영
        int hold = userStock.getHold() - count;
        userStock = UserStockEntity.builder()
                .id(userStock.getId())
                .user(user)
                .stock(stock)
                .hold(hold)
                .averagePrice(userStock.getAveragePrice())
                .bankruptcyNo(user.getBankruptcyNo())
                .build();
        userStockRepository.save(userStock);
        user = user.toBuilder().cash(user.getCash() + totalPrice).build();
        userRepository.save(user);
        ExecutionEntity execution = ExecutionEntity.builder()
                .user(user)
                .stock(stock)
                .price(price.longValue())
                .amount(count)
                .isBought(false)
                .dealAt(java.time.LocalDateTime.now())
                .bankruptcyNo(user.getBankruptcyNo())
                .build();
        executionRepository.save(execution);
        return count;
    }

    @Transactional
    public Boolean addInterest(Long userId, String stockId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        StockEntity stock = stockRepository.findById(stockId).orElseThrow(() -> new RuntimeException("Stock not found"));
        InterestEntity interest = interestRepository.findByUserAndStock(user, stock);
        if (interest == null) {
            interest = InterestEntity.builder().user(user).stock(stock).isInterested(true).build();
            interestRepository.save(interest);
        } else {
            interest.setIsInterested(true);
            interestRepository.save(interest);
        }
        return true;
    }

    @Transactional
    public Boolean cancelInterest(Long userId, String stockId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        StockEntity stock = stockRepository.findById(stockId).orElseThrow(() -> new RuntimeException("Stock not found"));
        InterestEntity interest = interestRepository.findByUserAndStock(user, stock);
        if (interest == null) {
            throw new RuntimeException("Interest not found");
        } else {
            interest.setIsInterested(false);
            interestRepository.save(interest);
        }
        return true;
    }
} 