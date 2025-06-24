package com.hsu_mafia.motoo.api.stock.service;

import com.hsu_mafia.motoo.api.stock.entity.UserStockEntity;
import com.hsu_mafia.motoo.api.stock.repository.UserStockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserStockService {
    private final UserStockRepository userStockRepository;

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
} 