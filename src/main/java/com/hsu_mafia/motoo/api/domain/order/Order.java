package com.hsu_mafia.motoo.api.domain.order;

import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.domain.stock.Stock;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Builder;
import lombok.AllArgsConstructor;
import com.hsu_mafia.motoo.global.common.BaseEntity;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder
@AllArgsConstructor
public class Order extends BaseEntity {
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
    private OrderType orderType; // BUY / SELL

    @Column(nullable = false)
    private Long quantity;

    @Column(nullable = false)
    private Long price; // 지정가

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private OrderStatus status; // PENDING / COMPLETED / CANCELLED

    public void updateStatus(OrderStatus status) {
        this.status = status;
    }
} 