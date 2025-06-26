package com.hsu_mafia.motoo.api.industry.service;

import com.hsu_mafia.motoo.api.industry.entity.IndustryEntity;
import com.hsu_mafia.motoo.api.industry.repository.IndustryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class IndustryService {
    private final IndustryRepository industryRepository;

    public List<IndustryEntity> findAll() {
        return industryRepository.findAll();
    }

    public Optional<IndustryEntity> findById(Long id) {
        return industryRepository.findById(id);
    }

    public IndustryEntity save(IndustryEntity industry) {
        return industryRepository.save(industry);
    }

    public void deleteById(Long id) {
        industryRepository.deleteById(id);
    }
} 