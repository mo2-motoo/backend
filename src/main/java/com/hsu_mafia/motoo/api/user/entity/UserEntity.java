package com.hsu_mafia.motoo.api.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.hsu_mafia.motoo.global.common.BaseEntity;


@Entity
@Table(name = "user")
@NoArgsConstructor
@Getter
public class UserEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String providerId;

    private Long seedMoney;

    private Long cash;

    private String profileImage;

    private String name;

    private Integer bankruptcyNo;

    private LocalDate lastQuizDate;

    private LocalDateTime joinAt;

}
