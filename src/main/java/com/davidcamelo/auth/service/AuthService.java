package com.davidcamelo.auth.service;

import com.davidcamelo.auth.dto.AuthRequest;
import com.davidcamelo.auth.dto.AuthResponse;
import com.davidcamelo.auth.dto.AuthTokenRequest;
import com.davidcamelo.auth.dto.RefreshTokenRequest;

public interface AuthService {
    AuthResponse login(AuthRequest authRequest);
    void logout(AuthTokenRequest authTokenRequest);
    AuthResponse refreshToken(RefreshTokenRequest refreshTokenRequest);
}
