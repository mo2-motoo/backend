package com.hsu_mafia.motoo.api.bankruptcy.repository;

import com.hsu_mafia.motoo.api.bankruptcy.entity.BankruptcyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankruptcyRepository extends JpaRepository<BankruptcyEntity, Long> {
} 