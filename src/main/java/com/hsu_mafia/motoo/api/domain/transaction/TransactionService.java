package com.hsu_mafia.motoo.api.domain.transaction;

import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.domain.user.UserRepository;
import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import com.hsu_mafia.motoo.global.exception.TransactionDateRangeException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    
    // 거래내역 조회 기간 제한 (6개월)
    private static final int MAX_TRANSACTION_MONTHS = 6;

    public List<TransactionHistory> getTransactions(Long userId, Pageable pageable) {
        return transactionRepository.findByUserId(userId, pageable);
    }
    
    public List<TransactionHistory> getTransactionsByDescription(Long userId, String description, Pageable pageable) {
        return transactionRepository.findByDescription(description, pageable);
    }
    
    public List<TransactionHistory> getTransactionsByDateRange(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        // 6개월 제한 체크
        validateDateRange(start, end);
        
        return transactionRepository.findByUserIdAndDateRange(userId, start, end, pageable);
    }
    
    /**
     * 거래내역 조회 기간이 6개월을 초과하는지 검증합니다.
     */
    private void validateDateRange(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            throw new BaseException(ErrorCode.INVALID_REQUEST);
        }
        
        if (start.isAfter(end)) {
            throw new BaseException(ErrorCode.INVALID_REQUEST, "시작일은 종료일보다 이전이어야 합니다.");
        }
        
        long monthsBetween = ChronoUnit.MONTHS.between(start, end);
        if (monthsBetween > MAX_TRANSACTION_MONTHS) {
            throw new TransactionDateRangeException(
                String.format("거래내역 조회 기간은 최대 %d개월까지 가능합니다. 요청된 기간: %d개월", 
                    MAX_TRANSACTION_MONTHS, monthsBetween + 1)
            );
        }
    }
} 