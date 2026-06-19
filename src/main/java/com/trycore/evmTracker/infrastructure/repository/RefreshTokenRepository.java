package com.trycore.evmTracker.infrastructure.repository;

import com.trycore.evmTracker.domain.model.RefreshToken;
import com.trycore.evmTracker.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    int deleteByUser(User user);
}
