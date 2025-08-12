package com.hsu_mafia.motoo.global.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hsu_mafia.motoo.global.config.KisConfig;
import com.hsu_mafia.motoo.global.constants.ApiConstants;
import com.hsu_mafia.motoo.global.dto.StockPriceResponse;
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

@RequiredArgsConstructor
@RestController
@RequestMapping("/mock-orders")
public class MockOrderController {

    private final KisConfig kisConfig;
    private final ObjectMapper mapper = new ObjectMapper();

    @PostMapping
    public ResponseEntity<StockPriceResponse> createMockOrders() {
        try {
            String accessToken = fetchAccessToken();
            StockPriceResponse response = fetchCurrentPrice(accessToken);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

    private String fetchAccessToken() throws Exception {
        String body = String.format("""
            {
              "grant_type": "client_credentials",
              "appkey": "%s",
              "appsecret": "%s"
            }
            """, kisConfig.getAppKey(), kisConfig.getAppSecret());

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConstants.KIS_TOKEN_URL))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        return mapper.readTree(response.body()).path("access_token").asText(null);
    }

    private StockPriceResponse fetchCurrentPrice(String token) throws Exception {
        String stockCode = "005930";
        String url = UriComponentsBuilder
                .fromHttpUrl(ApiConstants.KIS_DOMESTIC_STOCK_PRICE_URL)
                .queryParam("fid_cond_mrkt_div_code", "J")
                .queryParam("fid_input_iscd", stockCode)
                .toUriString();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("appkey", kisConfig.getAppKey())
                .header("appsecret", kisConfig.getAppSecret())
                .header("tr_id", "FHKST01010100")
                .header("custtype", "P")
                .GET()
                .build();

        HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
        JsonNode output = mapper.readTree(response.body()).path("output");

        return StockPriceResponse.builder()
                .stockCode(output.path("stck_shrn_iscd").asText())
                .currentPrice(output.path("stck_prpr").asText())
                .changeAmount(output.path("prdy_vrss").asText())
                .changeRate(output.path("prdy_ctrt").asText())
                .volume(output.path("acml_vol").asText())
                .open(output.path("stck_oprc").asText())
                .high(output.path("stck_hgpr").asText())
                .low(output.path("stck_lwpr").asText())
                .per(output.path("per").asText())
                .eps(output.path("eps").asText())
                .pbr(output.path("pbr").asText())
                .build();
    }
}
