package com.hsu_mafia.motoo.api.stock.dto.response;

import com.hsu_mafia.motoo.api.stock.entity.StockEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class StockVolumeRes {

    private StockEntity stock;
    private Long volume;

    @Builder
    public StockVolumeRes(StockEntity stock, Long volume) {
        this.stock = stock;
        this.volume = volume;
    }
}
