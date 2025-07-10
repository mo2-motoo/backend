package com.hsu_mafia.motoo.global.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "주식 시세 응답 DTO")
public class StockPriceResponse {

    @Schema(description = "종목코드")
    private final String stockCode;

    @Schema(description = "현재가")
    private final String currentPrice;

    @Schema(description = "전일 대비 가격")
    private final String changeAmount;

    @Schema(description = "등락률 (%)")
    private final String changeRate;

    @Schema(description = "거래량")
    private final String volume;

    @Schema(description = "시가")
    private final String open;

    @Schema(description = "고가")
    private final String high;

    @Schema(description = "저가")
    private final String low;

    @Schema(description = "PER")
    private final String per;

    @Schema(description = "EPS")
    private final String eps;

    @Schema(description = "PBR")
    private final String pbr;
}
