package com.hsu_mafia.motoo.api.domain.portfolio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserStockRepository extends JpaRepository<UserStock, Long> {
    // TODO: Add custom queries if needed
} 