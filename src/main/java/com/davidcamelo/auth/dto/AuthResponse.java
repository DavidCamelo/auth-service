package com.davidcamelo.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuthResponse (
        UserDTO user,
        Long accessTokenExpiration,
        Long refreshTokenExpiration,
        String accessToken,
        String refreshToken
) { }
