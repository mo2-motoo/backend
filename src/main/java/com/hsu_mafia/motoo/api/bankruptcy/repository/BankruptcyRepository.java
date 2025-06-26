package com.hsu_mafia.motoo.api.bankruptcy.repository;

import com.hsu_mafia.motoo.api.bankruptcy.entity.BankruptcyEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import java.util.List;

public interface BankruptcyRepository extends JpaRepository<BankruptcyEntity, Long> {
    List<BankruptcyEntity> findAllByUser(UserEntity user);
    BankruptcyEntity findByUserAndBankruptcyNo(UserEntity user, Integer bankruptcyNo);
} 