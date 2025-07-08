package com.hsu_mafia.motoo.api.domain.execution;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExecutionRepository extends JpaRepository<Execution, Long> {
    List<Execution> findByUserIdOrderByExecutedAtDesc(Long userId);
} 