package com.hsu_mafia.motoo.api.domain.portfolio;

import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.domain.stock.Stock;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hsu_mafia.motoo.global.common.BaseEntity;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class UserStock extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Long averageBuyPrice; // 평단가
} 