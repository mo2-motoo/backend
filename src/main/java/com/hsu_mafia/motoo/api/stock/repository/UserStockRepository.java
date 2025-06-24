package com.hsu_mafia.motoo.api.stock.repository;

import com.hsu_mafia.motoo.api.stock.entity.UserStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserStockRepository extends JpaRepository<UserStockEntity, Long> {
} 