package com.hsu_mafia.motoo.api.domain.token;

import com.hsu_mafia.motoo.api.dto.token.TokenRes;
import com.hsu_mafia.motoo.global.config.KisConfig;
import com.hsu_mafia.motoo.global.constants.ApiConstants;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TokenService {

    private final ObjectMapper objectMapper;
    private final TokenMapper tokenMapper;
    private final TokenRepository tokenRepository;
    private final KisConfig kisConfig;

    /**
     * 현재 유효한 토큰을 반환합니다.
     * 토큰이 없거나 만료된 경우 새로운 토큰을 발급받습니다.
     */
    public String getValidAccessToken() {
        Optional<Token> optionalToken = tokenRepository.findTopByOrderByIdDesc();
        LocalDateTime now = LocalDateTime.now();

        if (optionalToken.isEmpty() || optionalToken.get().getExpiration().isBefore(now)) {
            log.info("토큰이 없거나 만료되었습니다. 새로운 토큰을 발급받습니다.");
            requestNewAccessToken();
            return tokenRepository.findTopByOrderByIdDesc()
                    .orElseThrow(() -> new RuntimeException("토큰 발급에 실패했습니다."))
                    .getAccessToken();
        }

        return optionalToken.get().getAccessToken();
    }

    /**
     * 토큰 만료 오류 발생 시 호출되는 메서드
     * 기존 토큰을 폐기하고 새로운 토큰을 발급받습니다.
     */
    public String refreshTokenOnError() {
        log.info("토큰 만료 오류로 인한 토큰 갱신을 시작합니다.");

        // 기존 토큰 폐기
        revokeCurrentToken();

        // 새로운 토큰 발급
        requestNewAccessToken();

        return tokenRepository.findTopByOrderByIdDesc()
                .orElseThrow(() -> new RuntimeException("토큰 갱신에 실패했습니다."))
                .getAccessToken();
    }

    /**
     * 현재 토큰을 폐기합니다.
     */
    private void revokeCurrentToken() {
        Optional<Token> currentToken = tokenRepository.findTopByOrderByIdDesc();

        if (currentToken.isPresent()) {
            try {
                revokeTokenFromApi(currentToken.get().getAccessToken());
                log.info("기존 토큰을 성공적으로 폐기했습니다.");
            } catch (Exception e) {
                log.warn("토큰 폐기 중 오류가 발생했지만 계속 진행합니다: {}", e.getMessage());
            }
        }
    }

    /**
     * 한국투자증권 API를 통해 토큰을 폐기합니다.
     */
    private void revokeTokenFromApi(String accessToken) throws JsonProcessingException {
        String APP_KEY = kisConfig.getAppKey();
        String APP_SECRET = kisConfig.getAppSecret();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("appkey", APP_KEY);
        requestMap.put("appsecret", APP_SECRET);
        requestMap.put("token", accessToken);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    ApiConstants.KIS_REVOKE_URL,
                    HttpMethod.POST,
                    new HttpEntity<>(objectMapper.writeValueAsString(requestMap), httpHeaders),
                    String.class
            );

            log.info("토큰 폐기 API 응답: {}", response.getBody());
        } catch (Exception e) {
            log.error("토큰 폐기 API 호출 중 오류: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 새로운 액세스 토큰을 발급받습니다.
     */
    private void requestNewAccessToken() {
        try {
            requestAccessTokenFromApi();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("토큰 발급 중 JSON 처리 에러가 발생했습니다.", e);
        }
    }

    /**
     * 한국투자증권 API를 통해 새로운 토큰을 발급받습니다.
     */
    private void requestAccessTokenFromApi() throws JsonProcessingException {
        String APP_KEY = kisConfig.getAppKey();
        String APP_SECRET = kisConfig.getAppSecret();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("grant_type", "client_credentials");
        requestMap.put("appkey", APP_KEY);
        requestMap.put("appsecret", APP_SECRET);

        try {
            ResponseEntity<TokenRes> response = restTemplate.exchange(
                    ApiConstants.KIS_TOKEN_URL,
                    HttpMethod.POST,
                    new HttpEntity<>(objectMapper.writeValueAsString(requestMap), httpHeaders),
                    TokenRes.class
            );

            log.info("API 응답 전체: {}", response.getBody());
            log.info("API 응답 access_token: {}", response.getBody().getAccessToken());
            log.info("API 응답 access_token_token_expired: {}", response.getBody().getAccessTokenTokenExpired());

            Token token = tokenMapper.toToken(Objects.requireNonNull(response.getBody()));
            log.info("매핑된 Token 엔티티: {}", token);
            log.info("매핑된 Token accessToken: {}", token.getAccessToken());
            log.info("매핑된 Token expiration: {}", token.getExpiration());
            
            Token savedToken = tokenRepository.save(token);
            log.info("저장된 Token ID: {}", savedToken.getId());
            log.info("저장된 Token accessToken: {}", savedToken.getAccessToken());
            log.info("저장된 Token expiration: {}", savedToken.getExpiration());

            log.info("새로운 토큰을 발급받았습니다: {}, 만료기간: {}",
                    savedToken.getAccessToken(), savedToken.getExpiration());

        } catch (Exception e) {
            log.error("토큰 발급 API 호출 중 오류: {}", e.getMessage());
            throw e;
        }
    }

    /**
     * 기존 메서드 - 하위 호환성을 위해 유지
     * @deprecated getValidAccessToken() 사용을 권장합니다.
     */
    @Deprecated
    public void validateOrRefreshToken() {
        getValidAccessToken();
    }
}