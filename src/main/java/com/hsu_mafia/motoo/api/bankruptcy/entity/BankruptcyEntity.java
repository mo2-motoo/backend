package com.hsu_mafia.motoo.api.bankruptcy.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hsu_mafia.motoo.global.common.BaseEntity;

@Entity
@Table(name = "bankruptcy")
@NoArgsConstructor
@Getter
public class BankruptcyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private Integer bankruptcyNo;
    private Long lastCash;
    private Long lastSeedMoney;
    private Long lastTotalAsset;
    private Long netIncome;
    private Double roi;
    private LocalDateTime bankruptAt;
} 