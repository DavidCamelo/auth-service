package com.davidcamelo.auth.dto;

import lombok.Builder;

@Builder
public record AuthTokenRequest(
        String authToken
) { }
