package com.hsu_mafia.motoo.api.domain.portfolio;

import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserStockRepository extends JpaRepository<UserStock, Long> {
    
    // 사용자별 보유주식 + 페이지네이션
    @Query("SELECT us FROM UserStock us WHERE us.user.id = :userId ORDER BY us.id DESC")
    List<UserStock> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 사용자별 보유주식 + Slice
    @Query("SELECT us FROM UserStock us WHERE us.user.id = :userId ORDER BY us.id DESC")
    Slice<UserStock> findSliceByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 기존 메서드들 (하위 호환성을 위해 유지)
    List<UserStock> findByUser(User user);
    Optional<UserStock> findByUserAndStock(User user, Stock stock);
} 