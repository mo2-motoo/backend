package com.hsu_mafia.motoo.api.domain.stock;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockRepository extends JpaRepository<Stock, String> {
    List<Stock> findByStockNameContainingIgnoreCaseOrIdContainingIgnoreCase(String stockName, String stockId);
    List<Stock> findByMarketType(String marketType);
    List<Stock> findByIndustryNameContainingIgnoreCase(String industryName);
} 