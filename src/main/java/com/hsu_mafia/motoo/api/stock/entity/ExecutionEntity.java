package com.hsu_mafia.motoo.api.stock.entity;

import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import com.hsu_mafia.motoo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "execution")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class ExecutionEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private StockEntity stock;

    private Long price;
    private Integer amount;
    private Boolean isBought;
    private LocalDateTime dealAt;
    private Integer bankruptcyNo;
} 