package com.hsu_mafia.motoo.api.bankruptcy.entity;

import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import com.hsu_mafia.motoo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bankruptcy")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BankruptcyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private UserEntity user;

    private Integer bankruptcyNo;
    private Long lastCash;
    private Long lastSeedMoney;
    private Long lastTotalAsset;
    private Long netIncome;
    private Double roi;
    private LocalDateTime bankruptAt;
} 