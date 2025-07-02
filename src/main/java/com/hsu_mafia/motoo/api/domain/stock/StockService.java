package com.hsu_mafia.motoo.api.domain.stock;

import com.hsu_mafia.motoo.api.dto.stock.StockResponse;
import com.hsu_mafia.motoo.api.dto.stock.StockSearchRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class StockService {
    private final StockRepository stockRepository;

    public List<StockResponse> getStocks(Long userId) {
        // TODO: Implement logic
        return List.of();
    }

    public StockResponse getStock(Long userId, String stockId) {
        // TODO: Implement logic
        return null;
    }

    public List<StockResponse> searchStocks(Long userId, StockSearchRequest request) {
        // TODO: Implement logic
        return List.of();
    }
} 