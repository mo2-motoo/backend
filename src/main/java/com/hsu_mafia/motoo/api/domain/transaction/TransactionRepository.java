package com.hsu_mafia.motoo.api.domain.transaction;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import com.hsu_mafia.motoo.api.domain.user.User;

public interface TransactionRepository extends JpaRepository<TransactionHistory, Long> {
    
    // 사용자별 거래 + 페이지네이션
    @Query("SELECT t FROM TransactionHistory t WHERE t.user.id = :userId ORDER BY t.id DESC")
    List<TransactionHistory> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 사용자별 거래 + Slice
    @Query("SELECT t FROM TransactionHistory t WHERE t.user.id = :userId ORDER BY t.id DESC")
    Slice<TransactionHistory> findSliceByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 설명별 거래 + 페이지네이션
    @Query("SELECT t FROM TransactionHistory t WHERE t.description = :description ORDER BY t.id DESC")
    List<TransactionHistory> findByDescription(@Param("description") String description, Pageable pageable);
    
    // 설명별 거래 + Slice
    @Query("SELECT t FROM TransactionHistory t WHERE t.description = :description ORDER BY t.id DESC")
    Slice<TransactionHistory> findSliceByDescription(@Param("description") String description, Pageable pageable);
    
    // 사용자별 날짜 범위 거래 + 페이지네이션
    @Query("SELECT t FROM TransactionHistory t WHERE t.user.id = :userId AND t.createdAt BETWEEN :start AND :end ORDER BY t.id DESC")
    List<TransactionHistory> findByUserIdAndDateRange(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);
    
    // 사용자별 날짜 범위 거래 + Slice
    @Query("SELECT t FROM TransactionHistory t WHERE t.user.id = :userId AND t.createdAt BETWEEN :start AND :end ORDER BY t.id DESC")
    Slice<TransactionHistory> findSliceByUserIdAndDateRange(@Param("userId") Long userId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end, Pageable pageable);
    

} 