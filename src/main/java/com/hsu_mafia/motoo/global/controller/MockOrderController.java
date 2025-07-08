package com.hsu_mafia.motoo.global.controller;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mock-orders")
public class MockOrderController {

    @PostMapping
    public ResponseEntity<?> createMockOrders() {
        try {
            // 실제로는 환경변수나 설정파일에서 관리해야 함
            String APP_KEY = "";
            String APP_SECRET = "";
            String BASE_URL = "https://openapi.koreainvestment.com:9443";

            // 1. Access Token 발급
            HttpClient client = HttpClient.newHttpClient();
            String jsonBody = String.format("""
                {\n\t\"grant_type\": \"client_credentials\",\n\t\"appkey\": \"%s\",\n\t\"appsecret\": \"%s\"\n                }
                """, APP_KEY, APP_SECRET);
            HttpRequest tokenRequest = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + "/oauth2/tokenP"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            HttpResponse<String> tokenResponse = client.send(tokenRequest, HttpResponse.BodyHandlers.ofString());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(tokenResponse.body());
            String accessToken = rootNode.get("access_token").asText();

            // 2. 과거 주가 데이터 요청 (삼성전자 005930, 2024-01-01~2024-07-01)
            String stockCode = "005930";
            String startDate = "20240101";
            String endDate = "20240701";
            String url = UriComponentsBuilder.fromHttpUrl(BASE_URL + "/uapi/domestic-stock/v1/quotations/inquire-daily-price")
                    .queryParam("fid_cond_mrkt_div_code", "J")
                    .queryParam("fid_input_iscd", stockCode)
                    .queryParam("fid_org_adj_prc", "1")
                    .queryParam("fid_period_div_code", "D")
                    .queryParam("fid_buysell_div_code", "")
                    .queryParam("fid_input_date_1", startDate)
                    .queryParam("fid_input_date_2", endDate)
                    .queryParam("fid_sort_code", "D")
                    .toUriString();

            HttpRequest dataRequest = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("appkey", APP_KEY)
                    .header("appsecret", APP_SECRET)
                    .header("tr_id", "FHKST01010100") // 모의투자용
                    .header("custtype", "P") // 개인
                    .GET()
                    .build();

            HttpResponse<String> dataResponse = client.send(dataRequest, HttpResponse.BodyHandlers.ofString());
            JsonNode jsonNode = mapper.readTree(dataResponse.body());
            JsonNode outputs = jsonNode.get("output");

            List<Map<String, String>> result = new ArrayList<>();
            for (JsonNode node : outputs) {
                Map<String, String> item = new HashMap<>();
                item.put("date", node.get("stck_bsop_date").asText());
                item.put("close", node.get("stck_clpr").asText());
                item.put("volume", node.get("acml_vol").asText());
                result.add(item);
            }
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(e.getMessage());
        }
    }
}
