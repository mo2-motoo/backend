package com.hsu_mafia.motoo.api.word.repository;

import com.hsu_mafia.motoo.api.word.entity.WordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WordRepository extends JpaRepository<WordEntity, Long> {
} 