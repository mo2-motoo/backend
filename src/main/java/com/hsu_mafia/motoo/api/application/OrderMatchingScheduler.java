// package com.hsu_mafia.motoo.api.application;

// import com.hsu_mafia.motoo.api.domain.execution.ExecutionService;
// import com.hsu_mafia.motoo.api.domain.order.Order;
// import com.hsu_mafia.motoo.api.domain.order.OrderRepository;
// import com.hsu_mafia.motoo.api.domain.order.OrderStatus;
// import com.hsu_mafia.motoo.api.domain.order.OrderType;
// import com.hsu_mafia.motoo.global.util.PriceUtil;
// import java.util.List;
// import lombok.RequiredArgsConstructor;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Service;

// @Service
// @RequiredArgsConstructor
// public class OrderMatchingScheduler {
//     private final OrderRepository orderRepository;
//     private final ExecutionService executionService;
//     private final PriceUtil priceUtil;

//     // 1분마다 실행
//     @Scheduled(fixedRate = 60000)
//     public void matchOrders() {
//         List<Order> pendingOrders = orderRepository.findByStatus(OrderStatus.PENDING);

//         for (Order order : pendingOrders) {
//             Long currentPrice = priceUtil.getCurrentPrice(order.getStock().getId()).longValue();

//             boolean canExecute = false;
//             if (order.getOrderType() == OrderType.BUY) {
//                 // 매수: 주문가 >= 현재가
//                 canExecute = order.getPrice() >= currentPrice;
//             } else if (order.getOrderType() == OrderType.SELL) {
//                 // 매도: 주문가 <= 현재가
//                 canExecute = order.getPrice() <= currentPrice;
//             }

//             if (canExecute) {
//                 executionService.processOrderExecution(order.getId(), currentPrice);
//             }
//         }
//     }
// }