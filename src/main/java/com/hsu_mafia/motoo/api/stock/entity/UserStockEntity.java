package com.hsu_mafia.motoo.api.stock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_stock")
@NoArgsConstructor
@Getter
public class UserStockEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;
    private String stockId;
    private Integer hold;
    private Integer bankruptcyNo;
    private Integer averagePrice;
} 