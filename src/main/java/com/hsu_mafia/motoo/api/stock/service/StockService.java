package com.hsu_mafia.motoo.api.stock.service;

import com.hsu_mafia.motoo.api.stock.entity.StockEntity;
import com.hsu_mafia.motoo.api.stock.repository.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    public List<StockEntity> findAll() {
        return stockRepository.findAll();
    }

    public Optional<StockEntity> findById(String id) {
        return stockRepository.findById(id);
    }

    public StockEntity save(StockEntity stock) {
        return stockRepository.save(stock);
    }

    public void deleteById(String id) {
        stockRepository.deleteById(id);
    }
} 