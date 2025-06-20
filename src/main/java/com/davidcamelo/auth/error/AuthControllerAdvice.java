package com.davidcamelo.auth.error;

import com.davidcamelo.auth.api.AuthController;
import com.davidcamelo.auth.dto.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Date;

@RestControllerAdvice(assignableTypes = { AuthController.class })
public class AuthControllerAdvice {

    @ExceptionHandler(value = { AuthException.class })
    public ResponseEntity<ErrorDTO> handleAuthException(AuthException ex) {
        return new ResponseEntity<>(ErrorDTO.builder().message(ex.getMessage()).timestamp(new Date()).build(), HttpStatus.NOT_FOUND);
    }
}
