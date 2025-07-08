package com.hsu_mafia.motoo.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {
    private Long userId;
    private String username;
    private String email;
    private Long seedMoney;
    private Long cash;
    private Long totalValue;
    private Long netProfit;
    private LocalDateTime joinAt;
} 