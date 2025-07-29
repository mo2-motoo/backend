package com.hsu_mafia.motoo.global.constants;

public final class ApiConstants {
    
    private ApiConstants() {
        // 유틸리티 클래스이므로 인스턴스화 방지
    }
    
    // 한국투자증권 API URL
    public static final String KIS_BASE_URL = "https://openapi.koreainvestment.com:9443";
    public static final String KIS_TOKEN_URL = KIS_BASE_URL + "/oauth2/tokenP";
    public static final String KIS_REVOKE_URL = KIS_BASE_URL + "/oauth2/revokeP";
    
    // 국내주식 API
    public static final String KIS_DOMESTIC_STOCK_PRICE_URL = KIS_BASE_URL + "/uapi/domestic-stock/v1/quotations/inquire-price";
    public static final String KIS_KOSPI200_URL = KIS_BASE_URL + "/uapi/domestic-stock/v1/quotations/inquire-index-composition";
    public static final String KIS_MINUTE_CHART_URL = KIS_BASE_URL + "/uapi/domestic-stock/v1/quotations/inquire-time-itemchartprice";
    
    // 해외주식 API
    public static final String KIS_OVERSEAS_STOCK_PRICE_URL = KIS_BASE_URL + "/uapi/overseas-price/v1/quotations/price";
    
    // API 헤더 상수
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_APPKEY = "appkey";
    public static final String HEADER_APPSECRET = "appsecret";
    public static final String HEADER_TR_ID = "tr_id";
    public static final String HEADER_CUSTTYPE = "custtype";
    
    // API 거래 ID
    public static final String TR_ID_STOCK_PRICE = "FHKST01010100";
    public static final String TR_ID_KOSPI200 = "FHKST01010100";
    public static final String TR_ID_MINUTE_CHART = "FHKST03010200";
    public static final String TR_ID_OVERSEAS_PRICE = "HHDFS00000300";
    
    // API 파라미터
    public static final String MARKET_TYPE_KOSPI = "J";
    public static final String CUSTTYPE_PERSONAL = "P";
    
    // NASDAQ 상위 종목 조회 API (실제로는 한국투자증권 API에서 가져와야 함)
    public static final String KIS_NASDAQ_TOP_STOCKS_URL = KIS_BASE_URL + "/uapi/overseas-price/v1/quotations/inquire-price";
    public static final String TR_ID_NASDAQ_TOP = "HHDFS00000300";
    
    // 재무제표 API
    public static final String KIS_FINANCIAL_QUARTERLY_URL = KIS_BASE_URL + "/uapi/domestic-stock/v1/finance/inquire-finance";
    public static final String KIS_FINANCIAL_ANNUAL_URL = KIS_BASE_URL + "/uapi/domestic-stock/v1/finance/inquire-finance";
    public static final String TR_ID_FINANCIAL_QUARTERLY = "FHKST01010100";
    public static final String TR_ID_FINANCIAL_ANNUAL = "FHKST01010100";
    
    // 거래시간
    public static final int TRADING_START_HOUR = 9;
    public static final int TRADING_START_MINUTE = 0;
    public static final int TRADING_END_HOUR = 15;
    public static final int TRADING_END_MINUTE = 30;
    
    // API 호출 간격 (밀리초)
    public static final long API_CALL_INTERVAL = 100L;
} 