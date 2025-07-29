package com.hsu_mafia.motoo.api.domain.portfolio;

import com.hsu_mafia.motoo.api.domain.stock.Stock;
import com.hsu_mafia.motoo.api.domain.user.User;
import com.hsu_mafia.motoo.api.domain.user.UserRepository;
import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import com.hsu_mafia.motoo.global.util.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PortfolioService {
    private final UserStockRepository userStockRepository;
    private final UserRepository userRepository;
    private final PriceUtil priceUtil;

    public User getPortfolio(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // UserStock을 함께 로딩 (N+1 문제 방지)
        List<UserStock> userStocks = userStockRepository.findByUser(user);
        
        return user;
    }
    
    public Slice<UserStock> getUserStocks(Long userId, Pageable pageable) {
        return userStockRepository.findSliceByUserId(userId, pageable);
    }

} 