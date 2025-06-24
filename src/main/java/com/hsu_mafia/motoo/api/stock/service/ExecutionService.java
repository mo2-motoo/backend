package com.hsu_mafia.motoo.api.stock.service;

import com.hsu_mafia.motoo.api.stock.entity.ExecutionEntity;
import com.hsu_mafia.motoo.api.stock.repository.ExecutionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExecutionService {
    private final ExecutionRepository executionRepository;

    public List<ExecutionEntity> findAll() {
        return executionRepository.findAll();
    }

    public Optional<ExecutionEntity> findById(Long id) {
        return executionRepository.findById(id);
    }

    public ExecutionEntity save(ExecutionEntity execution) {
        return executionRepository.save(execution);
    }

    public void deleteById(Long id) {
        executionRepository.deleteById(id);
    }
} 