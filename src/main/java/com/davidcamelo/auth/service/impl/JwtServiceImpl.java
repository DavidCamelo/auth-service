package com.davidcamelo.auth.service.impl;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.davidcamelo.auth.service.JwtService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class JwtServiceImpl implements JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.access-token-expiration-ms}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration-ms}")
    private long refreshTokenExpiration;

    private Algorithm getAlgorithm () {
        return Algorithm.HMAC256(secret);
    }

    @Override
    public String generateAccessToken(String username, List<String> roles) {
        return JWT.create()
                .withSubject(username)
                .withClaim("roles", roles)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + accessTokenExpiration))
                .sign(getAlgorithm());
    }

    @Override
    public String generateRefreshToken(String username) {
        return JWT.create()
                .withSubject(username)
                .withIssuedAt(new Date(System.currentTimeMillis()))
                .withExpiresAt(new Date(System.currentTimeMillis() + refreshTokenExpiration))
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
