package com.hsu_mafia.motoo.api.presentation.token;

import com.hsu_mafia.motoo.api.domain.token.Token;
import com.hsu_mafia.motoo.api.domain.token.TokenService;
import com.hsu_mafia.motoo.global.common.CommonResponse;
import com.hsu_mafia.motoo.global.exception.ErrorCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/v1/token")
@RequiredArgsConstructor
@Tag(name = "Token", description = "한국투자증권 API 토큰 관리")
public class TokenController {
    
    private final TokenService tokenService;
    
    /**
     * 현재 토큰 상태 확인
     */
    @GetMapping("/status")
    @Operation(summary = "토큰 상태 확인", description = "현재 한국투자증권 API 토큰의 상태를 확인합니다.")
    public ResponseEntity<CommonResponse<TokenStatusResponse>> getTokenStatus() {
        try {
            String accessToken = tokenService.getValidAccessToken();
            return ResponseEntity.ok(CommonResponse.success(
                TokenStatusResponse.builder()
                    .hasValidToken(true)
                    .tokenPreview(accessToken.substring(0, 10) + "...")
                    .build()
            ));
        } catch (Exception e) {
            return ResponseEntity.ok(CommonResponse.success(
                TokenStatusResponse.builder()
                    .hasValidToken(false)
                    .errorMessage(e.getMessage())
                    .build()
            ));
        }
    }
    
    /**
     * 토큰 강제 갱신
     */
    @PostMapping("/refresh")
    @Operation(summary = "토큰 강제 갱신", description = "한국투자증권 API 토큰을 강제로 갱신합니다.")
    public ResponseEntity<CommonResponse<String>> refreshToken() {
        try {
            String newToken = tokenService.refreshTokenOnError();
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CommonResponse.success("토큰이 성공적으로 갱신되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }
    
    /**
     * 토큰 발급 테스트
     */
    @PostMapping("/test")
    @Operation(summary = "토큰 발급 테스트", description = "한국투자증권 API 토큰 발급을 테스트합니다.")
    public ResponseEntity<CommonResponse<String>> testTokenIssuance() {
        try {
            String accessToken = tokenService.getValidAccessToken();
            return ResponseEntity.ok(CommonResponse.success(
                "토큰 발급 성공: " + accessToken.substring(0, 20) + "..."
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonResponse.fail(ErrorCode.INTERNAL_SERVER_ERROR));
        }
    }
    
    /**
     * 토큰 상태 응답 DTO
     */
    public static class TokenStatusResponse {
        private boolean hasValidToken;
        private String tokenPreview;
        private String errorMessage;
        
        // Builder 패턴
        public static TokenStatusResponseBuilder builder() {
            return new TokenStatusResponseBuilder();
        }
        
        public static class TokenStatusResponseBuilder {
            private TokenStatusResponse response = new TokenStatusResponse();
            
            public TokenStatusResponseBuilder hasValidToken(boolean hasValidToken) {
                response.hasValidToken = hasValidToken;
                return this;
            }
            
            public TokenStatusResponseBuilder tokenPreview(String tokenPreview) {
                response.tokenPreview = tokenPreview;
                return this;
            }
            
            public TokenStatusResponseBuilder errorMessage(String errorMessage) {
                response.errorMessage = errorMessage;
                return this;
            }
            
            public TokenStatusResponse build() {
                return response;
            }
        }
        
        // Getters
        public boolean isHasValidToken() { return hasValidToken; }
        public String getTokenPreview() { return tokenPreview; }
        public String getErrorMessage() { return errorMessage; }
    }
} 