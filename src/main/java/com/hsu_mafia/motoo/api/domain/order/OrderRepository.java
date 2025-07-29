package com.hsu_mafia.motoo.api.domain.order;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // 사용자별 주문 + 페이지네이션
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.id DESC")
    List<Order> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 사용자별 주문 + Slice
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId ORDER BY o.id DESC")
    Slice<Order> findSliceByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 사용자별 상태별 주문 + 페이지네이션
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status ORDER BY o.id DESC")
    List<Order> findByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status, Pageable pageable);
    
    // 사용자별 상태별 주문 + Slice
    @Query("SELECT o FROM Order o WHERE o.user.id = :userId AND o.status = :status ORDER BY o.id DESC")
    Slice<Order> findSliceByUserIdAndStatus(@Param("userId") Long userId, @Param("status") OrderStatus status, Pageable pageable);
    
    // 상태별 주문 + 페이지네이션
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.id DESC")
    List<Order> findByStatus(@Param("status") OrderStatus status, Pageable pageable);
    
    // 상태별 주문 + Slice
    @Query("SELECT o FROM Order o WHERE o.status = :status ORDER BY o.id DESC")
    Slice<Order> findSliceByStatus(@Param("status") OrderStatus status, Pageable pageable);
    

} 