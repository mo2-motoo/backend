package com.hsu_mafia.motoo.api.stock.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class MinuteRes {

    private String time;
    private String price;
    private String amount;

    @Builder
    public MinuteRes(String time, String price, String amount) {
        this.time = time;
        this.price = price;
        this.amount = amount;
    }

    public void updateTime(String time) {
        this.time = time;
    }

    public void updatePrice(String price) {
        this.price = price;
    }

    public void updateAmount(String amount) {
        this.amount = amount;
    }
}
