package com.hsu_mafia.motoo.api.presentation.user;

import com.hsu_mafia.motoo.api.domain.user.UserService;
import com.hsu_mafia.motoo.api.dto.user.UserResponse;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public CommonResponse<UserResponse> getProfile() {
        Long userId = 1L; // TODO: 실제 인증된 사용자 ID로 변경
        UserResponse profile = userService.getProfile(userId);
        return CommonResponse.success(profile);
    }
} 