package com.hsu_mafia.motoo.api.interest.service;

import com.hsu_mafia.motoo.api.interest.entity.InterestEntity;
import com.hsu_mafia.motoo.api.interest.repository.InterestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InterestService {
    private final InterestRepository interestRepository;

    public List<InterestEntity> findAll() {
        return interestRepository.findAll();
    }

    public Optional<InterestEntity> findById(Long id) {
        return interestRepository.findById(id);
    }

    public InterestEntity save(InterestEntity interest) {
        return interestRepository.save(interest);
    }

    public void deleteById(Long id) {
        interestRepository.deleteById(id);
    }
} 