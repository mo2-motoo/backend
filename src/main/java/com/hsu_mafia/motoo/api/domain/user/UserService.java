package com.hsu_mafia.motoo.api.domain.user;

import com.hsu_mafia.motoo.api.dto.user.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse getProfile(Long userId) {
        // TODO: Implement logic
        return null;
    }
} 