package com.davidcamelo.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.davidcamelo.auth.config.JWTProperties;
import com.davidcamelo.auth.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JwtServiceImpl implements JwtService {
    private final JWTProperties jwtProperties;

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(jwtProperties.secret());
    }

    @Override
    public String generateAccessToken(String username, List<String> roles) {
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", roles)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration()))
                .sign(getAlgorithm());
    }

    @Override
    public String generateRefreshToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration()))
                .sign(getAlgorithm());
    }

    @Override
    public String validateRefreshTokenAndGetUsername(String token) {
        return JWT.require(getAlgorithm())
                .build()
                .verify(token)
                .getSubject();
    }
}
