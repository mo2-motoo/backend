package com.hsu_mafia.motoo.api.domain.execution;

import com.hsu_mafia.motoo.api.domain.order.Order;
import com.hsu_mafia.motoo.api.domain.order.OrderRepository;
import com.hsu_mafia.motoo.api.domain.order.OrderStatus;
import com.hsu_mafia.motoo.api.domain.order.OrderType;
import com.hsu_mafia.motoo.api.domain.portfolio.UserStock;
import com.hsu_mafia.motoo.api.domain.portfolio.UserStockRepository;
import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.domain.stock.StockRepository;
import com.hsu_mafia.motoo.api.domain.transaction.TransactionHistory;
import com.hsu_mafia.motoo.api.domain.transaction.TransactionService;
import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.domain.user.UserRepository;
import com.hsu_mafia.motoo.api.dto.order.OrderQueueMessage;
import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import com.hsu_mafia.motoo.global.util.PriceUtil;
import com.hsu_mafia.motoo.global.util.RedisQueueUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderExecutionService {
    
    private final OrderRepository orderRepository;
    private final ExecutionRepository executionRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserStockRepository userStockRepository;
    private final TransactionService transactionService;
    private final PriceUtil priceUtil;
    private final RedisQueueUtil redisQueueUtil;
    
    /**
     * 주문을 Redis Queue에 추가합니다.
     */
    public void addOrderToQueue(Order order) {
        try {
            OrderQueueMessage message = OrderQueueMessage.fromOrder(
                order.getId(),
                order.getUser().getId(),
                order.getStock().getStockCode(),
                order.getOrderType(),
                order.getQuantity(),
                order.getPrice(),
                order.getCreatedAt(),
                order.getPrice() == null // 시장가 주문 여부
            );
            
            redisQueueUtil.addToOrderQueue(message);
            log.info("주문이 체결 대기 큐에 추가되었습니다. OrderId: {}", order.getId());
        } catch (Exception e) {
            log.warn("Redis Queue 추가 실패. 주문은 생성되었지만 체결 대기 큐에 추가되지 않았습니다. OrderId: {}, Error: {}", 
                    order.getId(), e.getMessage());
            // Redis 연결 실패 시에도 주문 생성은 성공으로 처리
        }
    }
    
    /**
     * 대기 중인 모든 주문을 처리합니다.
     */
    public void processPendingOrders() {
        log.info("대기 중인 주문 처리 시작");
        
        // 현재 시세 데이터 수집
        collectCurrentPrices();
        
        // 대기 큐에서 주문들을 가져와서 처리
        List<Object> pendingOrders = redisQueueUtil.getAllFromOrderQueue();
        
        for (Object orderObj : pendingOrders) {
            if (orderObj instanceof OrderQueueMessage orderMessage) {
                processOrder(orderMessage);
            }
        }
        
        log.info("대기 중인 주문 처리 완료");
    }
    
    /**
     * 개별 주문을 처리합니다.
     */
    private void processOrder(OrderQueueMessage orderMessage) {
        try {
            // 주문 정보 조회
            Optional<Order> orderOpt = orderRepository.findById(orderMessage.getOrderId());
            if (orderOpt.isEmpty()) {
                log.warn("주문을 찾을 수 없습니다. OrderId: {}", orderMessage.getOrderId());
                return;
            }
            
            Order order = orderOpt.get();
            
            // 이미 처리된 주문인지 확인
            if (order.getStatus() != OrderStatus.PENDING) {
                log.info("이미 처리된 주문입니다. OrderId: {}, Status: {}", order.getId(), order.getStatus());
                return;
            }
            
            // 유효성 검사
            if (!validateOrder(order)) {
                log.warn("주문 유효성 검사 실패. OrderId: {}", order.getId());
                order.updateStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
                return;
            }
            
            // 체결 가능 여부 확인
            if (canExecuteOrder(order)) {
                executeOrder(order);
            }
            
        } catch (Exception e) {
            log.error("주문 처리 중 오류 발생. OrderId: {}", orderMessage.getOrderId(), e);
        }
    }
    
    /**
     * 주문 유효성을 검사합니다.
     */
    private boolean validateOrder(Order order) {
        User user = order.getUser();
        Stock stock = order.getStock();
        
        // 사용자 잔고 확인
        if (order.getOrderType() == OrderType.BUY) {
            Long requiredAmount = order.getQuantity() * order.getPrice();
            if (user.getCash() < requiredAmount) {
                log.warn("잔고 부족. UserId: {}, Required: {}, Available: {}", 
                    user.getId(), requiredAmount, user.getCash());
                return false;
            }
        } else if (order.getOrderType() == OrderType.SELL) {
            // 보유 주식 확인
            Optional<UserStock> userStockOpt = userStockRepository.findByUserIdAndStockCode(
                user.getId(), stock.getStockCode());
            
            if (userStockOpt.isEmpty() || userStockOpt.get().getQuantity() < order.getQuantity()) {
                log.warn("보유 주식 부족. UserId: {}, StockCode: {}, Required: {}, Available: {}", 
                    user.getId(), stock.getStockCode(), order.getQuantity(), 
                    userStockOpt.map(UserStock::getQuantity).orElse(0L));
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * 주문이 체결 가능한지 확인합니다.
     */
    private boolean canExecuteOrder(Order order) {
        Long currentPrice = priceUtil.getCurrentPrice(order.getStock().getStockCode());
        
        if (currentPrice == null) {
            log.warn("현재가 정보를 가져올 수 없습니다. StockCode: {}", order.getStock().getStockCode());
            return false;
        }
        
        // 시장가 주문은 항상 체결 가능
        if (order.getPrice() == null) {
            return true;
        }
        
        // 지정가 주문 체결 조건 확인
        if (order.getOrderType() == OrderType.BUY) {
            // 매수: 현재가 <= 주문가
            return currentPrice <= order.getPrice();
        } else {
            // 매도: 현재가 >= 주문가
            return currentPrice >= order.getPrice();
        }
    }
    
    /**
     * 주문을 체결합니다.
     */
    private void executeOrder(Order order) {
        Long currentPrice = priceUtil.getCurrentPrice(order.getStock().getStockCode());
        
        // 체결 정보 생성
        Execution execution = Execution.builder()
            .user(order.getUser())
            .stock(order.getStock())
            .orderType(order.getOrderType())
            .quantity(order.getQuantity())
            .executedPrice(currentPrice)
            .executedAt(LocalDateTime.now())
            .build();
        
        executionRepository.save(execution);
        
        // 주문 상태 업데이트
        order.updateStatus(OrderStatus.COMPLETED);
        orderRepository.save(order);
        
        // 사용자 자산 업데이트
        updateUserAssets(order, currentPrice);
        
        // 거래내역 생성
        createTransactionHistory(order, currentPrice);
        
        log.info("주문 체결 완료. OrderId: {}, ExecutionId: {}, Price: {}", 
            order.getId(), execution.getId(), currentPrice);
    }
    
    /**
     * 사용자 자산을 업데이트합니다.
     */
    private void updateUserAssets(Order order, Long executedPrice) {
        User user = order.getUser();
        Stock stock = order.getStock();
        
        if (order.getOrderType() == OrderType.BUY) {
            // 매수: 현금 차감, 주식 추가
            Long totalAmount = order.getQuantity() * executedPrice;
            user.updateCash(user.getCash() - totalAmount);
            userRepository.save(user);
            
            // 보유 주식 업데이트
            updateUserStock(user, stock, order.getQuantity(), executedPrice, true);
            
        } else {
            // 매도: 현금 추가, 주식 차감
            Long totalAmount = order.getQuantity() * executedPrice;
            user.updateCash(user.getCash() + totalAmount);
            userRepository.save(user);
            
            // 보유 주식 업데이트
            updateUserStock(user, stock, order.getQuantity(), executedPrice, false);
        }
    }
    
    /**
     * 사용자 보유 주식을 업데이트합니다.
     */
    private void updateUserStock(User user, Stock stock, Long quantity, Long price, boolean isBuy) {
        Optional<UserStock> userStockOpt = userStockRepository.findByUserIdAndStockCode(
            user.getId(), stock.getStockCode());
        
        if (isBuy) {
            if (userStockOpt.isPresent()) {
                // 기존 보유 주식이 있는 경우 평단가 계산
                UserStock existingStock = userStockOpt.get();
                Long totalQuantity = existingStock.getQuantity() + quantity;
                Long totalCost = (existingStock.getQuantity() * existingStock.getAverageBuyPrice()) + (quantity * price);
                Long newAveragePrice = totalCost / totalQuantity;
                
                existingStock.updateQuantity(totalQuantity);
                existingStock.updateAverageBuyPrice(newAveragePrice);
                userStockRepository.save(existingStock);
            } else {
                // 새로운 주식 보유
                UserStock newUserStock = UserStock.builder()
                    .user(user)
                    .stock(stock)
                    .quantity(quantity)
                    .averageBuyPrice(price)
                    .build();
                userStockRepository.save(newUserStock);
            }
        } else {
            // 매도: 보유 수량 차감
            UserStock existingStock = userStockOpt.get();
            Long remainingQuantity = existingStock.getQuantity() - quantity;
            
            if (remainingQuantity <= 0) {
                // 모든 주식을 매도한 경우
                userStockRepository.delete(existingStock);
            } else {
                // 일부 주식을 매도한 경우
                existingStock.updateQuantity(remainingQuantity);
                userStockRepository.save(existingStock);
            }
        }
    }
    
    /**
     * 거래내역을 생성합니다.
     */
    private void createTransactionHistory(Order order, Long executedPrice) {
        String description = order.getOrderType() == OrderType.BUY ? "매수 체결" : "매도 체결";
        Long amount = order.getQuantity() * executedPrice;
        
        // 매도인 경우 양수로, 매수인 경우 음수로 기록
        if (order.getOrderType() == OrderType.BUY) {
            amount = -amount;
        }
        
        TransactionHistory transaction = TransactionHistory.builder()
            .user(order.getUser())
            .amount(amount)
            .description(description)
            .build();
        
        // TransactionHistory 저장
        transactionService.saveTransaction(transaction);
        log.info("거래내역 생성 완료. Amount: {}, Description: {}", amount, description);
    }
    
    /**
     * 현재 시세 데이터를 수집합니다.
     */
    private void collectCurrentPrices() {
        // 이미 StockDataCollectionService에서 분단위로 수집되고 있으므로
        // 여기서는 별도 처리하지 않음
        log.debug("현재 시세 데이터 수집 완료");
    }
} 