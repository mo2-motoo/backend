package com.hsu_mafia.motoo.api.domain.execution;

import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.domain.order.OrderType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import com.hsu_mafia.motoo.global.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Execution extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stock_id")
    private Stock stock;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private OrderType orderType;

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Long executedPrice;

    @Column(nullable = false)
    private LocalDateTime executedAt;
} 