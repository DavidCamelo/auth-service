package com.davidcamelo.auth.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.util.Date;

@Builder
public record ErrorDTO(
        String message,
        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        Date timestamp
) { }
