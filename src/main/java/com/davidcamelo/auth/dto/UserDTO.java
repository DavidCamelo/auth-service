package com.davidcamelo.auth.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

import java.util.List;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserDTO(
        String name,
        String lastName,
        String email,
        String username,
        List<String> roles
) { }
