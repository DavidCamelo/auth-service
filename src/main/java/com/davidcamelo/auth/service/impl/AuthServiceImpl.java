package com.davidcamelo.auth.service.impl;

import com.davidcamelo.auth.config.JWTProperties;
import com.davidcamelo.auth.dto.AuthRequest;
import com.davidcamelo.auth.dto.AuthResponse;
import com.davidcamelo.auth.dto.AuthTokenRequest;
import com.davidcamelo.auth.dto.ErrorDTO;
import com.davidcamelo.auth.dto.RefreshTokenRequest;
import com.davidcamelo.auth.error.AuthException;
import com.davidcamelo.auth.service.AuthService;
import com.davidcamelo.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;
    private final JWTProperties jwtProperties;

    @Override
    public AuthResponse login(AuthRequest authRequest) {
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        if (authentication.isAuthenticated()) {
            var username = authRequest.username();
            var roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            return AuthResponse.builder()
                    .accessTokenExpiration(jwtProperties.accessTokenExpiration())
                    .refreshTokenExpiration(jwtProperties.refreshTokenExpiration())
                    .accessToken(jwtService.generateAccessToken(username, roles))
                    .refreshToken(jwtService.generateRefreshToken(username))
                    .build();
        } else {
            throw new AuthException(ErrorDTO.builder().message("Authentication failed").timestamp(new Date()).build());
        }
    }

    @Override
    public void logout(AuthTokenRequest authTokenRequest) {
    }

    @Override
    public AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest) {
        try {
            var username = jwtService.validateRefreshTokenAndGetUsername(refreshTokenRequest.refreshToken());
            var userDetails = userDetailsService.loadUserByUsername(username);
            var roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            var newAccessToken = jwtService.generateAccessToken(username, roles);
            return AuthResponse.builder()
                    .accessTokenExpiration(jwtProperties.accessTokenExpiration())
                    .accessToken(jwtService.generateAccessToken(username, roles))
                    .build();
        } catch (Exception e) {
            throw new AuthException(ErrorDTO.builder().message("Invalid refresh token").timestamp(new Date()).build());
        }
    }
}
