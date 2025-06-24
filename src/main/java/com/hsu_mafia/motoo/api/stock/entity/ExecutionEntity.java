package com.hsu_mafia.motoo.api.stock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "execution")
@NoArgsConstructor
@Getter
public class ExecutionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stockId;
    private Long userId;
    private Long price;
    private Integer amount;
    private Boolean isBought;
    private LocalDateTime dealAt;
    private Integer bankruptcyNo;
} 