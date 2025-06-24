package com.hsu_mafia.motoo.api.stock.repository;

import com.hsu_mafia.motoo.api.stock.entity.ExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExecutionRepository extends JpaRepository<ExecutionEntity, Long> {
} 