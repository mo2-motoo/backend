package com.hsu_mafia.motoo.api.domain.stock;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu_mafia.motoo.api.domain.token.TokenService;
import com.hsu_mafia.motoo.global.config.KisConfig;
import com.hsu_mafia.motoo.global.constants.ApiConstants;
import com.hsu_mafia.motoo.global.exception.StockApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockApiService {

    private final TokenService tokenService;
    private final RestTemplate restTemplate;
    private final KisConfig kisConfig;
    private final ObjectMapper objectMapper;

    /**
     * 주식 현재가 데이터를 가져옵니다.
     * 토큰 만료 시 자동으로 토큰을 갱신하고 재시도합니다.
     */
    public JsonNode getStockCurrentPrice(String stockCode) {
        return callKisApiWithTokenRetry(() -> fetchStockCurrentPrice(stockCode));
    }

    /**
     * 해외주식 현재가 데이터를 가져옵니다.
     */
    public JsonNode getOverseasStockCurrentPrice(String stockCode) {
        return callKisApiWithTokenRetry(() -> fetchOverseasStockCurrentPrice(stockCode));
    }

    /**
     * KOSPI200 구성종목 리스트를 가져옵니다.
     */
    public JsonNode getKospi200Stocks() {
        return callKisApiWithTokenRetry(() -> fetchKospi200Stocks());
    }

    /**
     * 주식 분봉 데이터를 가져옵니다.
     */
    public JsonNode getStockMinuteData(String stockCode, LocalDateTime startTime, LocalDateTime endTime) {
        return callKisApiWithTokenRetry(() -> fetchStockMinuteData(stockCode, startTime, endTime));
    }

    /**
     * NASDAQ 상위 종목 리스트를 가져옵니다.
     */
    public JsonNode getNasdaqTopStocks() {
        return callKisApiWithTokenRetry(() -> fetchNasdaqTopStocks());
    }

    /**
     * 분기별 재무제표 데이터를 가져옵니다.
     */
    public JsonNode getQuarterlyFinancialData(String stockCode) {
        return callKisApiWithTokenRetry(() -> fetchQuarterlyFinancialData(stockCode));
    }

    /**
     * 연간 재무제표 데이터를 가져옵니다.
     */
    public JsonNode getAnnualFinancialData(String stockCode) {
        return callKisApiWithTokenRetry(() -> fetchAnnualFinancialData(stockCode));
    }

    /**
     * 한국투자증권 API 호출 시 토큰 만료 오류 처리를 위한 공통 메서드
     */
    private JsonNode callKisApiWithTokenRetry(ApiCall apiCall) {
        try {
            return apiCall.execute();
        } catch (HttpClientErrorException e) {
            // 토큰 만료 오류 코드 확인 (한국투자증권 API 문서 참조)
            if (isTokenExpiredError(e)) {
                log.info("토큰 만료 오류 감지. 토큰을 갱신하고 재시도합니다.");

                // 토큰 갱신
                tokenService.refreshTokenOnError();

                // API 재호출
                try {
                    return apiCall.execute();
                } catch (Exception retryException) {
                    throw new StockApiException("토큰 갱신 후 재시도에서도 오류 발생: " + retryException.getMessage());
                }
            } else {
                log.error("토큰 만료가 아닌 다른 API 오류: {}", e.getMessage());
                throw e;
            }
        }
    }

    /**
     * 국내주식 현재가를 가져오는 메서드
     */
    private JsonNode fetchStockCurrentPrice(String stockCode) {
        String accessToken = tokenService.getValidAccessToken();
        String appkey = kisConfig.getAppKey();
        String appsecret = kisConfig.getAppSecret();

        HttpHeaders headers = createHeaders(accessToken, appkey, appsecret, ApiConstants.TR_ID_STOCK_PRICE);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder
                .fromHttpUrl(ApiConstants.KIS_DOMESTIC_STOCK_PRICE_URL)
                .queryParam("fid_cond_mrkt_div_code", ApiConstants.MARKET_TYPE_KOSPI)
                .queryParam("fid_input_iscd", stockCode)
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return parseResponse(response.getBody());
    }

    /**
     * 해외주식 현재가를 가져오는 메서드
     */
    private JsonNode fetchOverseasStockCurrentPrice(String stockCode) {
        String accessToken = tokenService.getValidAccessToken();
        String appkey = kisConfig.getAppKey();
        String appsecret = kisConfig.getAppSecret();

        HttpHeaders headers = createHeaders(accessToken, appkey, appsecret, ApiConstants.TR_ID_OVERSEAS_PRICE);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder
                .fromHttpUrl(ApiConstants.KIS_OVERSEAS_STOCK_PRICE_URL)
                .queryParam("AUTH", "")
                .queryParam("EXCD", "NAS")
                .queryParam("SYMB", stockCode)
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return parseResponse(response.getBody());
    }

    /**
     * KOSPI200 구성종목 리스트를 가져오는 메서드
     */
    private JsonNode fetchKospi200Stocks() {
        String accessToken = tokenService.getValidAccessToken();
        String appkey = kisConfig.getAppKey();
        String appsecret = kisConfig.getAppSecret();

        HttpHeaders headers = createHeaders(accessToken, appkey, appsecret, ApiConstants.TR_ID_KOSPI200);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder
                .fromHttpUrl(ApiConstants.KIS_KOSPI200_URL)
                .queryParam("fid_cond_mrkt_div_code", ApiConstants.MARKET_TYPE_KOSPI)
                .queryParam("fid_input_iscd", "0001")
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return parseResponse(response.getBody());
    }

    /**
     * 주식 분봉 데이터를 가져오는 메서드
     */
    private JsonNode fetchStockMinuteData(String stockCode, LocalDateTime startTime, LocalDateTime endTime) {
        String accessToken = tokenService.getValidAccessToken();
        String appkey = kisConfig.getAppKey();
        String appsecret = kisConfig.getAppSecret();

        HttpHeaders headers = createHeaders(accessToken, appkey, appsecret, ApiConstants.TR_ID_MINUTE_CHART);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String startTimeStr = startTime.format(formatter);

        String url = UriComponentsBuilder
                .fromHttpUrl(ApiConstants.KIS_MINUTE_CHART_URL)
                .queryParam("fid_etc_cls_code", "")
                .queryParam("fid_cond_mrkt_div_code", ApiConstants.MARKET_TYPE_KOSPI)
                .queryParam("fid_input_iscd", stockCode)
                .queryParam("fid_input_hour_1", startTimeStr)
                .queryParam("fid_pw_data_incu_yn", "Y")
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return parseResponse(response.getBody());
    }

    /**
     * NASDAQ 상위 종목 리스트를 가져오는 메서드
     */
    private JsonNode fetchNasdaqTopStocks() {
        String accessToken = tokenService.getValidAccessToken();
        String appkey = kisConfig.getAppKey();
        String appsecret = kisConfig.getAppSecret();

        HttpHeaders headers = createHeaders(accessToken, appkey, appsecret, ApiConstants.TR_ID_NASDAQ_TOP);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder
                .fromHttpUrl(ApiConstants.KIS_NASDAQ_TOP_STOCKS_URL)
                .queryParam("AUTH", "")
                .queryParam("EXCD", "NAS")
                .queryParam("SYMB", "TOP30") // 상위 30개 종목 조회
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return parseResponse(response.getBody());
    }

    /**
     * 토큰 만료 오류인지 확인합니다.
     * 한국투자증권 API 문서에 따른 오류 코드를 확인해야 합니다.
     */
    private boolean isTokenExpiredError(HttpClientErrorException e) {
        // HTTP 상태 코드 확인
        if (e.getStatusCode().value() == 401) {
            return true;
        }

        // 응답 본문에서 특정 오류 메시지 확인
        String responseBody = e.getResponseBodyAsString();

        // 한국투자증권 API에서 토큰 만료 시 반환하는 메시지들
        return responseBody.contains("토큰이 만료되었습니다") ||
                responseBody.contains("APBK0917") || // 토큰 만료 오류 코드 (예시)
                responseBody.contains("유효하지 않은 토큰") ||
                responseBody.contains("token expired");
    }

    /**
     * API 호출을 위한 함수형 인터페이스
     */
    @FunctionalInterface
    private interface ApiCall {
        JsonNode execute();
    }

    /**
     * API 헤더를 생성하는 헬퍼 메서드
     */
    private HttpHeaders createHeaders(String accessToken, String appkey, String appsecret, String trId) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(ApiConstants.HEADER_AUTHORIZATION, "Bearer " + accessToken);
        headers.set(ApiConstants.HEADER_APPKEY, appkey);
        headers.set(ApiConstants.HEADER_APPSECRET, appsecret);
        headers.set(ApiConstants.HEADER_TR_ID, trId);
        return headers;
    }

    /**
     * API 응답을 파싱하는 헬퍼 메서드
     */
    private JsonNode parseResponse(String responseBody) {
        try {
            return objectMapper.readTree(responseBody);
        } catch (Exception e) {
            throw new StockApiException("응답 데이터 파싱에 실패했습니다: " + e.getMessage());
        }
    }

    /**
     * 분기별 재무제표 데이터를 가져오는 메서드
     */
    private JsonNode fetchQuarterlyFinancialData(String stockCode) {
        String accessToken = tokenService.getValidAccessToken();
        String appkey = kisConfig.getAppKey();
        String appsecret = kisConfig.getAppSecret();

        HttpHeaders headers = createHeaders(accessToken, appkey, appsecret, ApiConstants.TR_ID_FINANCIAL_QUARTERLY);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder
                .fromHttpUrl(ApiConstants.KIS_FINANCIAL_QUARTERLY_URL)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                .queryParam("FID_INPUT_ISCD", stockCode)
                .queryParam("FID_INPUT_PRICE_1", "")
                .queryParam("FID_INPUT_PRICE_2", "")
                .queryParam("FID_VOL_CNT", "")
                .queryParam("FID_INPUT_DATE_1", "")
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return parseResponse(response.getBody());
    }

    /**
     * 연간 재무제표 데이터를 가져오는 메서드
     */
    private JsonNode fetchAnnualFinancialData(String stockCode) {
        String accessToken = tokenService.getValidAccessToken();
        String appkey = kisConfig.getAppKey();
        String appsecret = kisConfig.getAppSecret();

        HttpHeaders headers = createHeaders(accessToken, appkey, appsecret, ApiConstants.TR_ID_FINANCIAL_ANNUAL);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        String url = UriComponentsBuilder
                .fromHttpUrl(ApiConstants.KIS_FINANCIAL_ANNUAL_URL)
                .queryParam("FID_COND_MRKT_DIV_CODE", "J")
                .queryParam("FID_INPUT_ISCD", stockCode)
                .queryParam("FID_INPUT_PRICE_1", "")
                .queryParam("FID_INPUT_PRICE_2", "")
                .queryParam("FID_VOL_CNT", "")
                .queryParam("FID_INPUT_DATE_1", "")
                .toUriString();

        ResponseEntity<String> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                String.class
        );

        return parseResponse(response.getBody());
    }

    /**
     * 기존 메서드 - 하위 호환성을 위해 유지
     * @deprecated getStockCurrentPrice() 사용을 권장합니다.
     */
    @Deprecated
    public String getStockPrice(String stockCode) {
        JsonNode response = getStockCurrentPrice(stockCode);
        return response.toString();
    }
}