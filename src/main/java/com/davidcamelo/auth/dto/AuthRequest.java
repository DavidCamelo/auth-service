package com.davidcamelo.auth.dto;

import lombok.Builder;

@Builder
public record AuthRequest (
        String username,
        String password
) { }
