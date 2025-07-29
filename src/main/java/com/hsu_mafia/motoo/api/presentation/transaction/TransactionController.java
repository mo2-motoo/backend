package com.hsu_mafia.motoo.api.presentation.transaction;

import com.hsu_mafia.motoo.api.domain.transaction.TransactionHistory;
import com.hsu_mafia.motoo.api.domain.transaction.TransactionService;
import com.hsu_mafia.motoo.api.dto.transaction.TransactionResponse;
import com.hsu_mafia.motoo.api.dto.transaction.TransactionSearchRequest;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Transaction", description = "거래내역 API")
@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {
    
    private final TransactionService transactionService;
    
    /**
     * 사용자의 거래내역을 페이지네이션으로 조회합니다.
     */
    @GetMapping
    @Operation(summary = "거래내역 조회", description = "사용자의 거래내역을 페이지네이션으로 조회합니다.")
    public ResponseEntity<CommonResponse<List<TransactionResponse>>> getTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // TODO: 실제 사용자 ID는 JWT 토큰에서 추출
        Long userId = 1L; // 임시 사용자 ID
        
        Pageable pageable = PageRequest.of(page, size);
        List<TransactionHistory> transactions = transactionService.getTransactions(userId, pageable);
        
        List<TransactionResponse> responses = transactions.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(CommonResponse.success(responses));
    }
    
    /**
     * 설명으로 거래내역을 검색합니다.
     */
    @GetMapping("/search")
    @Operation(summary = "거래내역 검색", description = "설명으로 거래내역을 검색합니다.")
    public ResponseEntity<CommonResponse<List<TransactionResponse>>> searchTransactions(
            @RequestParam String description,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // TODO: 실제 사용자 ID는 JWT 토큰에서 추출
        Long userId = 1L; // 임시 사용자 ID
        
        Pageable pageable = PageRequest.of(page, size);
        List<TransactionHistory> transactions = transactionService.getTransactionsByDescription(userId, description, pageable);
        
        List<TransactionResponse> responses = transactions.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(CommonResponse.success(responses));
    }
    
    /**
     * 기간별 거래내역을 조회합니다. (최대 6개월)
     */
    @GetMapping("/date-range")
    @Operation(summary = "기간별 거래내역 조회", description = "지정된 기간의 거래내역을 조회합니다. (최대 6개월)")
    public ResponseEntity<CommonResponse<List<TransactionResponse>>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        // TODO: 실제 사용자 ID는 JWT 토큰에서 추출
        Long userId = 1L; // 임시 사용자 ID
        
        Pageable pageable = PageRequest.of(page, size);
        List<TransactionHistory> transactions = transactionService.getTransactionsByDateRange(userId, startDate, endDate, pageable);
        
        List<TransactionResponse> responses = transactions.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(CommonResponse.success(responses));
    }
    
    /**
     * 복합 검색 조건으로 거래내역을 조회합니다.
     */
    @PostMapping("/search")
    @Operation(summary = "복합 검색", description = "설명과 기간을 포함한 복합 검색 조건으로 거래내역을 조회합니다.")
    public ResponseEntity<CommonResponse<List<TransactionResponse>>> searchTransactionsWithConditions(
            @RequestBody TransactionSearchRequest request) {
        
        // TODO: 실제 사용자 ID는 JWT 토큰에서 추출
        Long userId = 1L; // 임시 사용자 ID
        
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        List<TransactionHistory> transactions;
        
        if (request.getStartDate() != null && request.getEndDate() != null) {
            // 기간과 설명 모두 있는 경우
            transactions = transactionService.getTransactionsByDateRange(userId, request.getStartDate(), request.getEndDate(), pageable);
            
            // 설명 필터링 (기간 조회 후 메모리에서 필터링)
            if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
                transactions = transactions.stream()
                        .filter(t -> t.getDescription().contains(request.getDescription()))
                        .collect(Collectors.toList());
            }
        } else if (request.getDescription() != null && !request.getDescription().trim().isEmpty()) {
            // 설명만 있는 경우
            transactions = transactionService.getTransactionsByDescription(userId, request.getDescription(), pageable);
        } else {
            // 조건이 없는 경우 전체 조회
            transactions = transactionService.getTransactions(userId, pageable);
        }
        
        List<TransactionResponse> responses = transactions.stream()
                .map(TransactionResponse::from)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(CommonResponse.success(responses));
    }
}
