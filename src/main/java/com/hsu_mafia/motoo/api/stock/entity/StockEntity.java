package com.hsu_mafia.motoo.api.stock.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "stock")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class StockEntity {

    @Id
    private String id;

    private String stockName;

    private String outline;

    private String eps;

    private String bps;

    private Long totalStock;

    private String imageUrl;

}
