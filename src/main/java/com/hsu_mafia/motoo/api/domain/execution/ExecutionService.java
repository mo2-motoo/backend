package com.hsu_mafia.motoo.api.domain.execution;

import com.hsu_mafia.motoo.api.domain.order.Order;
import com.hsu_mafia.motoo.api.domain.order.OrderRepository;
import com.hsu_mafia.motoo.api.domain.order.OrderStatus;
import com.hsu_mafia.motoo.api.domain.portfolio.UserStock;
import com.hsu_mafia.motoo.api.domain.portfolio.UserStockRepository;
import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.domain.user.UserRepository;
import com.hsu_mafia.motoo.api.dto.execution.ExecutionDto;
import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExecutionService {
    private final ExecutionRepository executionRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;

    public List<ExecutionDto> getExecutions(Long userId) {
        List<Execution> executions = executionRepository.findByUserIdOrderByExecutedAtDesc(userId);
        
        return executions.stream()
                .map(this::convertToExecutionDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void processOrderExecution(Long orderId, Long executedPrice) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BaseException(ErrorCode.ORDER_NOT_FOUND));

        if (order.getStatus() != OrderStatus.PENDING) {
            return; // 이미 처리된 주문이거나 취소된 주문
        }

        User user = order.getUser();
        Long totalAmount = executedPrice * order.getQuantity();

        if (order.getOrderType().name().equals("BUY")) {
            // 매수 체결 처리
            if (user.getCash() < totalAmount) {
                throw new BaseException(ErrorCode.INSUFFICIENT_CASH);
            }

            // 현금 차감
            user.updateCash(user.getCash() - totalAmount);

            // 보유 주식 업데이트
            updateUserStock(user, order.getStock(), order.getQuantity(), executedPrice);

        } else if (order.getOrderType().name().equals("SELL")) {
            // 매도 체결 처리
            UserStock userStock = userStockRepository.findByUserAndStock(user, order.getStock())
                    .orElseThrow(() -> new BaseException(ErrorCode.INSUFFICIENT_STOCK));

            if (userStock.getQuantity() < order.getQuantity()) {
                throw new BaseException(ErrorCode.INSUFFICIENT_STOCK);
            }

            // 보유 주식 차감
            userStock.updateQuantity(userStock.getQuantity() - order.getQuantity());

            // 현금 증가
            user.updateCash(user.getCash() + totalAmount);
        }

        // 체결 내역 생성
        Execution execution = Execution.builder()
                .user(user)
                .stock(order.getStock())
                .orderType(order.getOrderType())
                .quantity(order.getQuantity())
                .executedPrice(executedPrice)
                .executedAt(LocalDateTime.now())
                .build();

        executionRepository.save(execution);

        // 주문 상태 업데이트
        order.updateStatus(OrderStatus.COMPLETED);
    }

    private void updateUserStock(User user, com.hsu_mafia.motoo.api.domain.stock.Stock stock, Long quantity, Long executedPrice) {
        Optional<UserStock> existingUserStock = userStockRepository.findByUserAndStock(user, stock);

        if (existingUserStock.isPresent()) {
            // 기존 보유 주식이 있는 경우 평단가 계산
            UserStock userStock = existingUserStock.get();
            Long totalQuantity = userStock.getQuantity() + quantity;
            Long totalCost = (userStock.getAverageBuyPrice() * userStock.getQuantity()) + (executedPrice * quantity);
            Long newAveragePrice = totalCost / totalQuantity;

            userStock.updateQuantity(totalQuantity);
            userStock.updateAverageBuyPrice(newAveragePrice);
        } else {
            // 새로운 주식 보유
            UserStock newUserStock = UserStock.builder()
                    .user(user)
                    .stock(stock)
                    .quantity(quantity)
                    .averageBuyPrice(executedPrice)
                    .build();

            userStockRepository.save(newUserStock);
        }
    }

    private ExecutionDto convertToExecutionDto(Execution execution) {
        return ExecutionDto.builder()
                .executionId(execution.getId())
                .stockId(execution.getStock().getId())
                .stockName(execution.getStock().getStockName())
                .orderType(execution.getOrderType())
                .quantity(execution.getQuantity())
                .executedPrice(execution.getExecutedPrice())
                .executedAt(execution.getExecutedAt())
                .build();
    }
} 