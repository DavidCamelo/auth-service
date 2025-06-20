package com.davidcamelo.auth.api;

import com.davidcamelo.auth.dto.AuthRequest;
import com.davidcamelo.auth.dto.AuthResponse;
import com.davidcamelo.auth.dto.ErrorDTO;
import com.davidcamelo.auth.dto.RefreshTokenRequest;
import com.davidcamelo.auth.dto.RefreshTokenResponse;
import com.davidcamelo.auth.error.AuthException;
import com.davidcamelo.auth.service.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.stream.Collectors;

@Tag(name = "Auth API")
@RestController
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

    @Operation(summary = "login", description = "User login authentication")
    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest authRequest) {
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.username(), authRequest.password()));
        if (authentication.isAuthenticated()) {
            var username = authRequest.username();
            var roles = authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            return AuthResponse.builder()
                    .accessToken(jwtService.generateAccessToken(username, roles))
                    .refreshToken(jwtService.generateRefreshToken(username))
                    .build();
        } else {
            throw new AuthException(ErrorDTO.builder().message("Authentication failed").timestamp(new Date()).build());
        }
    }

    @Operation(summary = "refresh", description = "Refresh token")
    @PostMapping("/refresh")
    public RefreshTokenResponse refresh(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            var username = jwtService.validateRefreshTokenAndGetUsername(refreshTokenRequest.refreshToken());
            var userDetails = userDetailsService.loadUserByUsername(username);
            var roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());
            var newAccessToken = jwtService.generateAccessToken(username, roles);
            return RefreshTokenResponse.builder().accessToken(newAccessToken).build();
        } catch (Exception e) {
            throw new AuthException(ErrorDTO.builder().message("Invalid refresh token").timestamp(new Date()).build());
        }
    }
}
