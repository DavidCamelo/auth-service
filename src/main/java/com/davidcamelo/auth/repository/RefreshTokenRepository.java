package com.davidcamelo.auth.repository;

import com.davidcamelo.auth.entity.RefreshToken;
import com.davidcamelo.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByTokenAndUser(String token, User user);
    Optional<RefreshToken> findByToken(String token);
}
