package com.davidcamelo.auth.dto;

import lombok.Builder;

@Builder
public record AuthResponse (
        String accessToken,
        String refreshToken
) { }
