package com.hsu_mafia.motoo.api.word.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hsu_mafia.motoo.global.common.BaseEntity;

@Entity
@Table(name = "word")
@NoArgsConstructor
@Getter
public class WordEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;

    private String meaning;
} 