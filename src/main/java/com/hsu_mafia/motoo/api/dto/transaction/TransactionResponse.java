package com.hsu_mafia.motoo.api.dto.transaction;

import com.hsu_mafia.motoo.api.domain.transaction.TransactionHistory;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionResponse {
    private Long id;
    private Long userId;
    private Long amount;
    private String description;
    private LocalDateTime createdAt;
    
    public static TransactionResponse from(TransactionHistory transaction) {
        return TransactionResponse.builder()
                .id(transaction.getId())
                .userId(transaction.getUser().getId())
                .amount(transaction.getAmount())
                .description(transaction.getDescription())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
} 