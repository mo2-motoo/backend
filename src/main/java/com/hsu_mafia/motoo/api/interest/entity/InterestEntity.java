package com.hsu_mafia.motoo.api.interest.entity;

import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import com.hsu_mafia.motoo.api.stock.entity.StockEntity;
import com.hsu_mafia.motoo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "interest")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class InterestEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private StockEntity stock;

    private Boolean isInterested;
} 