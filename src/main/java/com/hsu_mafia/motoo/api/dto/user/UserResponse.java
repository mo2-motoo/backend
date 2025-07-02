package com.hsu_mafia.motoo.api.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String username;
    private String email;
    private Long cash;
    private Long seedMoney;
    // TODO: Add more fields as needed
} 