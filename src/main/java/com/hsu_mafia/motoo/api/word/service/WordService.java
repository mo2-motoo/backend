package com.hsu_mafia.motoo.api.word.service;

import com.hsu_mafia.motoo.api.word.entity.WordEntity;
import com.hsu_mafia.motoo.api.word.repository.WordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WordService {
    private final WordRepository wordRepository;

    public List<WordEntity> findAll() {
        return wordRepository.findAll();
    }

    public Optional<WordEntity> findById(Long id) {
        return wordRepository.findById(id);
    }

    public WordEntity save(WordEntity word) {
        return wordRepository.save(word);
    }

    public void deleteById(Long id) {
        wordRepository.deleteById(id);
    }
} 