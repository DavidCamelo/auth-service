package com.davidcamelo.auth.service.impl;

import com.davidcamelo.auth.dto.ErrorDTO;
import com.davidcamelo.auth.entity.RefreshToken;
import com.davidcamelo.auth.entity.User;
import com.davidcamelo.auth.error.AuthException;
import com.davidcamelo.auth.repository.RefreshTokenRepository;
import com.davidcamelo.auth.repository.UserRepository;
import com.davidcamelo.auth.service.JwtService;
import com.davidcamelo.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    public RefreshToken createRefreshToken(String username) {
        var refreshToken = new RefreshToken();
        refreshToken.setUser(getUserByUsername(username));
        refreshToken.setToken(jwtService.generateRefreshToken(username));
        return refreshTokenRepository.save(refreshToken);
    }

    @Override
    public Optional<RefreshToken> verifyExpiration(String token) {
        var username = jwtService.validateRefreshTokenAndGetUsername(token);
        var user = getUserByUsername(username);
        return refreshTokenRepository.findByTokenAndUser(token, user);
    }

    @Override
    public void deleteRefreshToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(refreshTokenRepository::delete);
    }

    private User getUserByUsername(String username) {
        if (username.contains("@")) {
            return userRepository.findByEmail(username)
                    .orElseThrow(() -> new AuthException(ErrorDTO.builder().message("User not found").timestamp(new Date()).build()));
        }
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthException(ErrorDTO.builder().message("User not found").timestamp(new Date()).build()));
    }
}
