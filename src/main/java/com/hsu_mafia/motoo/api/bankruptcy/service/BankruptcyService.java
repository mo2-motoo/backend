package com.hsu_mafia.motoo.api.bankruptcy.service;

import com.hsu_mafia.motoo.api.bankruptcy.entity.BankruptcyEntity;
import com.hsu_mafia.motoo.api.bankruptcy.repository.BankruptcyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BankruptcyService {
    private final BankruptcyRepository bankruptcyRepository;

    public List<BankruptcyEntity> findAll() {
        return bankruptcyRepository.findAll();
    }

    public Optional<BankruptcyEntity> findById(Long id) {
        return bankruptcyRepository.findById(id);
    }

    public BankruptcyEntity save(BankruptcyEntity bankruptcy) {
        return bankruptcyRepository.save(bankruptcy);
    }

    public void deleteById(Long id) {
        bankruptcyRepository.deleteById(id);
    }
} 