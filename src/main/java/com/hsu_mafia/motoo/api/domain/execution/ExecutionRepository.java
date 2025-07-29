package com.hsu_mafia.motoo.api.domain.execution;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecutionRepository extends JpaRepository<Execution, Long> {
    
    // 사용자별 체결 + 페이지네이션
    @Query("SELECT e FROM Execution e WHERE e.user.id = :userId ORDER BY e.id DESC")
    List<Execution> findByUserId(@Param("userId") Long userId, Pageable pageable);
    
    // 사용자별 체결 + Slice
    @Query("SELECT e FROM Execution e WHERE e.user.id = :userId ORDER BY e.id DESC")
    Slice<Execution> findSliceByUserId(@Param("userId") Long userId, Pageable pageable);
    

} 