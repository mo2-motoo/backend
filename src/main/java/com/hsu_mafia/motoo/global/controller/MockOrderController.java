package com.hsu_mafia.motoo.global.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu_mafia.motoo.global.config.KisConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/mock-orders")
public class MockOrderController {

    private final KisConfig kisConfig;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<?> createMockOrders() {
        try {
            String accessToken = fetchAccessToken();
            List<Map<String, String>> prices = fetchDailyPrice(accessToken);
            return ResponseEntity.ok(prices);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    private String fetchAccessToken() throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String jsonBody = String.format("""
            {
              "grant_type": "client_credentials",
              "appkey": "%s",
              "appsecret": "%s"
            }
            """, kisConfig.getAppKey(), kisConfig.getAppSecret());

        System.out.println("[DEBUG] Token Request Body: " + jsonBody);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(kisConfig.getBaseUrl() + "/oauth2/tokenP"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] Token API Response: " + response.body());

        JsonNode root = mapper.readTree(response.body());
        String token = root.path("access_token").asText(null);

        if (token == null) {
            throw new RuntimeException("access_token not found. Response: " + response.body());
        }

        return token;
    }

    private List<Map<String, String>> fetchDailyPrice(String accessToken) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String stockCode = "005930";  // 삼성전자
        String startDate = "20240101";
        String endDate = "20240701";

        String url = UriComponentsBuilder.fromHttpUrl(kisConfig.getBaseUrl() + "/uapi/domestic-stock/v1/quotations/inquire-daily-price")
                .queryParam("fid_cond_mrkt_div_code", "J")
                .queryParam("fid_input_iscd", stockCode)
                .queryParam("fid_org_adj_prc", "1")
                .queryParam("fid_period_div_code", "D")
                .queryParam("fid_buysell_div_code", "")
                .queryParam("fid_input_date_1", startDate)
                .queryParam("fid_input_date_2", endDate)
                .queryParam("fid_sort_code", "D")
                .toUriString();

        System.out.println("[DEBUG] Data Request URL: " + url);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + accessToken)
                .header("appkey", kisConfig.getAppKey())
                .header("appsecret", kisConfig.getAppSecret())
                .header("tr_id", "FHKST01010100") // 모의투자
                .header("custtype", "P")         // 개인
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        System.out.println("[DEBUG] Data API Response: " + response.body());

        JsonNode root = mapper.readTree(response.body());
        JsonNode outputs = root.path("output");

        if (outputs.isMissingNode()) {
            throw new RuntimeException("output not found in response: " + response.body());
        }

        List<Map<String, String>> results = new ArrayList<>();

        if (outputs.isArray()) {
            for (JsonNode node : outputs) {
                results.add(extractStockInfo(node));
            }
        } else if (outputs.isObject()) {
            results.add(extractStockInfo(outputs));
        } else {
            throw new RuntimeException("Unexpected output format: " + outputs.toString());
        }

        return results;
    }

    private Map<String, String> extractStockInfo(JsonNode node) {
        Map<String, String> item = new HashMap<>();
        item.put("date", node.path("stck_bsop_date").asText(null));
        item.put("close", node.path("stck_clpr").asText(null));
        item.put("volume", node.path("acml_vol").asText(null));
        return item;
    }
}
