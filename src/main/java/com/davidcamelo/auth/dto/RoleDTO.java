package com.davidcamelo.auth.dto;

import lombok.Builder;

@Builder
public record RoleDTO (
        String name
) { }
