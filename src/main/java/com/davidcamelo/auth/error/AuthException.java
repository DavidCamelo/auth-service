package com.davidcamelo.auth.error;

import com.davidcamelo.auth.dto.ErrorDTO;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AuthException extends RuntimeException {

    public AuthException(ErrorDTO errorDTO) {
        super(errorDTO.message());
        log.error("Error message: {}, timestamp: {}", errorDTO.message(), errorDTO.timestamp(), this);
    }
}
