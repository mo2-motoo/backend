package com.hsu_mafia.motoo.api.presentation.user;

import com.hsu_mafia.motoo.api.dto.user.UserResponse;
import com.hsu_mafia.motoo.api.domain.user.UserService;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "유저 관련 API")
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "내 정보 조회", description = "사용자 프로필 정보를 조회합니다.")
    public CommonResponse<UserResponse> getProfile() {
        Long userId = 1L;
        return CommonResponse.success(userService.getProfile(userId));
    }
} 