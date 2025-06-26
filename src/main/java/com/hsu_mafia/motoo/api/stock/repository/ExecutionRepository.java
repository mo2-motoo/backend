package com.hsu_mafia.motoo.api.stock.repository;

import com.hsu_mafia.motoo.api.stock.entity.ExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import java.util.List;

public interface ExecutionRepository extends JpaRepository<ExecutionEntity, Long> {
    List<ExecutionEntity> findAllByUserAndBankruptcyNo(UserEntity user, Integer bankruptcyNo);
} 