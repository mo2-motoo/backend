package com.hsu_mafia.motoo.api.domain.order;

import com.hsu_mafia.motoo.api.domain.execution.OrderExecutionService;
import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.domain.stock.StockRepository;
import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.domain.user.UserRepository;
import com.hsu_mafia.motoo.api.domain.portfolio.UserStock;
import com.hsu_mafia.motoo.api.domain.portfolio.UserStockRepository;
import com.hsu_mafia.motoo.api.dto.order.OrderRequest;
import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final UserStockRepository userStockRepository;
    private final OrderExecutionService orderExecutionService;

    @Transactional
    public void placeOrder(Long userId, OrderRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));
        
        Stock stock = stockRepository.findById(request.getStockId())
                .orElseThrow(() -> new BaseException(ErrorCode.STOCK_NOT_FOUND));

        // 매수 주문인 경우 현금 확인
        if (request.getOrderType() == OrderType.BUY) {
            BigDecimal requiredAmount = request.getPrice().multiply(new BigDecimal(request.getQuantity()));
            if (user.getCash() < requiredAmount.longValue()) {
                throw new BaseException(ErrorCode.INSUFFICIENT_CASH);
            }
        }

        // 매도 주문인 경우 보유 주식 확인
        if (request.getOrderType() == OrderType.SELL) {
            Optional<UserStock> userStock = userStockRepository.findByUserAndStock(user, stock);
            if (userStock.isEmpty() || userStock.get().getQuantity() < request.getQuantity()) {
                throw new BaseException(ErrorCode.INSUFFICIENT_STOCK);
            }
        }

        Order order = Order.builder()
                .user(user)
                .stock(stock)
                .orderType(request.getOrderType())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .createdAt(LocalDateTime.now())
                .status(OrderStatus.PENDING)
                .build();

        orderRepository.save(order);
        
        // Redis Queue에 주문 추가
        orderExecutionService.addOrderToQueue(order);
    }

    public List<Order> getOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserId(userId, pageable);
    }
    
    public List<Order> getOrdersByStatus(Long userId, OrderStatus status, Pageable pageable) {
        return orderRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    @Transactional
    public void cancelOrder(Long userId, Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BaseException(ErrorCode.ORDER_NOT_FOUND));

        // 주문 소유자 확인
        if (!order.getUser().getId().equals(userId)) {
            throw new BaseException(ErrorCode.ORDER_ACCESS_DENIED);
        }

        // 대기 중인 주문만 취소 가능
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new BaseException(ErrorCode.ORDER_CANNOT_CANCEL);
        }

        order.updateStatus(OrderStatus.CANCELLED);
    }
} 