package com.hsu_mafia.motoo.api.domain.transaction;

import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.domain.user.UserRepository;
import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public List<TransactionHistory> getTransactions(Long userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable);
    }
    
    public List<TransactionHistory> getTransactionsByDescription(Long userId, String description, Pageable pageable) {
        return transactionRepository.findByDescription(description, pageable);
    }
    
    public List<TransactionHistory> getTransactionsByDateRange(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return transactionRepository.findByUserIdAndDateRange(userId, start, end, pageable);
    }
} 