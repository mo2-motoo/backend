package com.hsu_mafia.motoo.api.stock.repository;

import com.hsu_mafia.motoo.api.stock.entity.StockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<StockEntity, String> {
} 