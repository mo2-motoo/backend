package com.hsu_mafia.motoo.api.stock.service;

import com.hsu_mafia.motoo.api.stock.entity.ExecutionEntity;
import com.hsu_mafia.motoo.api.stock.repository.ExecutionRepository;
import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import com.hsu_mafia.motoo.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExecutionService {
    private final ExecutionRepository executionRepository;
    private final UserRepository userRepository;

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

    public List<ExecutionEntity> myAllExecution(Long userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return executionRepository.findAllByUserAndBankruptcyNo(user, user.getBankruptcyNo());
    }

    public List<ExecutionEntity> myExecution(Long userId, String stockId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        return executionRepository.findAllByUserAndBankruptcyNo(user, user.getBankruptcyNo())
                .stream().filter(e -> e.getStock().getId().equals(stockId)).toList();
    }
} 