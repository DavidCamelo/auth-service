package com.davidcamelo.auth.dto;

import lombok.Builder;

@Builder
public record SignUpRequest(
        String name,
        String lastName,
        String email,
        String password
) { }
