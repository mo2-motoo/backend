package com.hsu_mafia.motoo.api.stock.repository;

import com.hsu_mafia.motoo.api.stock.entity.UserStockEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import java.util.List;

public interface UserStockRepository extends JpaRepository<UserStockEntity, Long> {
    List<UserStockEntity> findAllByUserAndBankruptcyNo(UserEntity user, Integer bankruptcyNo);
} 