package com.hsu_mafia.motoo.api.domain.user;

import com.hsu_mafia.motoo.api.domain.portfolio.UserStock;
import com.hsu_mafia.motoo.api.domain.portfolio.UserStockRepository;
import com.hsu_mafia.motoo.global.exception.BaseException;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import com.hsu_mafia.motoo.global.util.PriceUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {
    private final UserRepository userRepository;
    private final UserStockRepository userStockRepository;
    private final PriceUtil priceUtil;

    public User getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new BaseException(ErrorCode.USER_NOT_FOUND));

        // UserStock을 함께 로딩 (N+1 문제 방지)
        List<UserStock> userStocks = userStockRepository.findByUser(user);
        
        return user;
    }
} 