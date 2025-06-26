package com.hsu_mafia.motoo.api.industry.entity;

import com.hsu_mafia.motoo.api.stock.entity.StockEntity;
import com.hsu_mafia.motoo.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "industry")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class IndustryEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String wics;
    private String industryClass;

    @OneToMany(mappedBy = "industry", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StockEntity> stocks = new ArrayList<>();
} 