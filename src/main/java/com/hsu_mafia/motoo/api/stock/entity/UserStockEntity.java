package com.hsu_mafia.motoo.api.stock.entity;

import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import com.hsu_mafia.motoo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_stock")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class UserStockEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private StockEntity stock;

    private Integer hold;
    private Integer bankruptcyNo;
    private Integer averagePrice;
} 