package com.hsu_mafia.motoo.global.util;

import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class PriceUtil {
    private final Map<String, Integer> stockIndex = Map.ofEntries(
        Map.entry("010950", 0),
        Map.entry("005930", 1),
        Map.entry("000660", 2),
        Map.entry("035720", 3),
        Map.entry("035420", 4),
        Map.entry("034220", 5),
        Map.entry("036570", 6),
        Map.entry("251270", 7),
        Map.entry("005490", 8),
        Map.entry("000880", 9),
        Map.entry("352820", 10),
        Map.entry("090430", 11),
        Map.entry("003490", 12),
        Map.entry("005380", 13),
        Map.entry("139480", 14),
        Map.entry("028260", 15),
        Map.entry("097950", 16),
        Map.entry("000080", 17),
        Map.entry("068270", 18),
        Map.entry("207940", 19),
        Map.entry("000720", 20),
        Map.entry("047040", 21),
        Map.entry("006360", 22),
        Map.entry("096770", 23),
        Map.entry("034020", 24),
        Map.entry("015760", 25),
        Map.entry("017670", 26),
        Map.entry("030200", 27),
        Map.entry("032640", 28),
        Map.entry("051900", 29),
        Map.entry("373220", 30),
        Map.entry("000120", 31)
    );

    public Integer getStockIndex(String stockId) {
        return this.stockIndex.get(stockId);
    }

    // 더미 메서드들 (기존 서비스 코드 컴파일용)
    public Long getCurrentPrice(String stockId) { return 0L; }
    public String getPriceDifference(String stockId) { return ""; }
    public String getRateDifference(String stockId) { return ""; }
    public Long getTradingVolume(String stockId) { return 0L; }
    public Integer getMin52(String stockId) { return 0; }
    public Integer getMax52(String stockId) { return 0; }
    public String getPer(String stockId) { return ""; }
    public String getPbr(String stockId) { return ""; }
}
