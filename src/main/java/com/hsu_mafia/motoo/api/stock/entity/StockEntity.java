package com.hsu_mafia.motoo.api.stock.entity;

import com.hsu_mafia.motoo.api.industry.entity.IndustryEntity;
import com.hsu_mafia.motoo.api.interest.entity.InterestEntity;
import com.hsu_mafia.motoo.api.stock.entity.ExecutionEntity;
import com.hsu_mafia.motoo.api.stock.entity.UserStockEntity;
import com.hsu_mafia.motoo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class StockEntity extends BaseEntity {
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "industry_id")
    private IndustryEntity industry;

    private String stockName;
    private String outline;
    private String eps;
    private String bps;
    private Long totalStock;
    private String imageUrl;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserStockEntity> userStocks = new ArrayList<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExecutionEntity> executions = new ArrayList<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<InterestEntity> interests = new ArrayList<>();
}
