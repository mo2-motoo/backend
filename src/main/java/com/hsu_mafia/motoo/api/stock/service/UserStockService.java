package com.hsu_mafia.motoo.api.stock.service;

import com.hsu_mafia.motoo.api.stock.entity.UserStockEntity;
import com.hsu_mafia.motoo.api.stock.repository.UserStockRepository;
import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import com.hsu_mafia.motoo.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserStockService {
    private final UserStockRepository userStockRepository;
    private final UserRepository userRepository;

    public List<UserStockEntity> findAll() {
        return userStockRepository.findAll();
    }

    public Optional<UserStockEntity> findById(Long id) {
        return userStockRepository.findById(id);
    }

    public UserStockEntity save(UserStockEntity userStock) {
        return userStockRepository.save(userStock);
    }

    public void deleteById(Long id) {
        userStockRepository.deleteById(id);
    }

    public List<UserStockEntity> myStocks(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return userStockRepository.findAllByUserAndBankruptcyNo(user, user.getBankruptcyNo());
    }

    public UserStockEntity myStock(Long userId, String stockId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return userStockRepository.findAllByUserAndBankruptcyNo(user, user.getBankruptcyNo())
                .stream().filter(us -> us.getStock().getId().equals(stockId)).findFirst()
                .orElseThrow(() -> new RuntimeException("UserStock not found"));
    }
} 