package com.hsu_mafia.motoo.api.domain.portfolio;

import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStockRepository extends JpaRepository<UserStock, Long> {
    List<UserStock> findByUser(User user);
    Optional<UserStock> findByUserAndStock(User user, Stock stock);
} 