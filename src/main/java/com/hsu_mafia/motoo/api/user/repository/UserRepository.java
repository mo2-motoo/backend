package com.hsu_mafia.motoo.api.user.repository;

import com.hsu_mafia.motoo.api.user.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
} 