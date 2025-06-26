package com.davidcamelo.auth.service.impl;

import com.davidcamelo.auth.config.JWTProperties;
import com.davidcamelo.auth.dto.AuthRequest;
import com.davidcamelo.auth.dto.AuthResponse;
import com.davidcamelo.auth.dto.ErrorDTO;
import com.davidcamelo.auth.dto.RefreshTokenRequest;
import com.davidcamelo.auth.entity.RefreshToken;
import com.davidcamelo.auth.error.AuthException;
import com.davidcamelo.auth.service.AuthService;
import com.davidcamelo.auth.service.JwtService;
import com.davidcamelo.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final JWTProperties jwtProperties;

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        if (authentication.isAuthenticated()) {
            var username = authRequest.username();
            var roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .toList();
            return AuthResponse.builder()
                    .accessTokenExpiration(jwtProperties.accessTokenExpiration())
                    .refreshTokenExpiration(jwtProperties.refreshTokenExpiration())
                    .accessToken(jwtService.generateAccessToken(username, roles))
                    .refreshToken(refreshTokenService.createRefreshToken(username).getToken())
                    .build();
        } else {
            throw new AuthException(ErrorDTO.builder().message("Authentication failed").timestamp(new Date()).build());
        }
    }

    @Override
    public void logout(RefreshTokenRequest refreshTokenRequest) {
        refreshTokenService.deleteRefreshToken(refreshTokenRequest.refreshToken());
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        return refreshTokenService.verifyExpiration(refreshTokenRequest.refreshToken())
                .map(RefreshToken::getUser)
                .map(user -> AuthResponse.builder()
                        .accessTokenExpiration(jwtProperties.accessTokenExpiration())
                        .accessToken(jwtService.generateAccessToken(user.getUsername(), user.getRoles().stream().toList()))
                        .build())
                .orElseThrow(() -> {
                    refreshTokenService.deleteRefreshToken(refreshTokenRequest.refreshToken());
                    return new AuthException(ErrorDTO.builder().message("Invalid refresh token").timestamp(new Date()).build());
                });
    }
}
