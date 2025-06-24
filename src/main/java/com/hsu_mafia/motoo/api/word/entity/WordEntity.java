package com.hsu_mafia.motoo.api.word.entity;

import com.hsu_mafia.motoo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "word")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class WordEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String word;
    private String meaning;
} 