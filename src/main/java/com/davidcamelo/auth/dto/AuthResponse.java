package com.davidcamelo.auth.dto;

import lombok.Builder;

@Builder
public record AuthResponse (
        long accessTokenExpiration,
        long refreshTokenExpiration,
        String accessToken,
        String refreshToken
) { }
