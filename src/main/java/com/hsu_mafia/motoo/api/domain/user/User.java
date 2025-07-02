package com.hsu_mafia.motoo.api.domain.user;

import com.hsu_mafia.motoo.api.domain.execution.Execution;
import com.hsu_mafia.motoo.api.domain.portfolio.UserStock;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hsu_mafia.motoo.global.common.BaseEntity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String username;

    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @Column(nullable = false)
    private Long seedMoney;

    @Column(nullable = false)
    private Long cash;

    @Column(nullable = false)
    private LocalDateTime joinAt;

    // 예: bankrupt 횟수나 마지막 퀴즈일 같은 추가 필드도 포함 가능

    // 연관관계
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserStock> stocks = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Execution> executions = new ArrayList<>();
} 