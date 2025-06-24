package com.hsu_mafia.motoo.api.industry.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hsu_mafia.motoo.global.common.BaseEntity;

@Entity
@Table(name = "industry")
@NoArgsConstructor
@Getter
public class IndustryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String wics;

    private String industryClass;
} 