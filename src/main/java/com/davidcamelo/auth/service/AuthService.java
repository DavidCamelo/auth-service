package com.davidcamelo.auth.service;

import com.davidcamelo.auth.dto.AuthResponse;

public interface AuthService {
    AuthResponse signUp(String name, String lastName, String email, String password);
    AuthResponse login(String username, String password);
    void logout(String refreshToken);
    AuthResponse refreshToken(String refreshToken);
}
