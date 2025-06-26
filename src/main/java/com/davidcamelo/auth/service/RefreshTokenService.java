package com.davidcamelo.auth.service;

import com.davidcamelo.auth.entity.RefreshToken;

import java.util.Optional;

public interface RefreshTokenService {
    RefreshToken createRefreshToken(String username);
    Optional<RefreshToken> verifyExpiration(String refreshToken);
    void deleteRefreshToken(String refreshToken);
}
