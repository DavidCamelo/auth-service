package com.davidcamelo.auth.service.impl;

import com.davidcamelo.auth.config.JWTProperties;
import com.davidcamelo.auth.dto.AuthResponse;
import com.davidcamelo.auth.dto.ErrorDTO;
import com.davidcamelo.auth.entity.RefreshToken;
import com.davidcamelo.auth.entity.Role;
import com.davidcamelo.auth.entity.User;
import com.davidcamelo.auth.error.AuthException;
import com.davidcamelo.auth.repository.RoleRepository;
import com.davidcamelo.auth.repository.UserRepository;
import com.davidcamelo.auth.service.AuthService;
import com.davidcamelo.auth.service.JwtService;
import com.davidcamelo.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final JWTProperties jwtProperties;

    @Override
    public AuthResponse signUp(String name, String lastName, String email, String password) {
        if (userRepository.findByEmail(email).isEmpty()) {
            User user = new User();
            user.setName(name);
            user.setLastName(lastName);
            user.setEmail(email);
            user.setUsername(email.split("@")[0]);
            user.setPassword(passwordEncoder.encode(password));
            var userRole = roleRepository.findByName("ROLE_USER").orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));
            user.setRoles(Set.of(userRole));
            userRepository.save(user);
            return login(email, password);
        }
        throw new AuthException(ErrorDTO.builder().message("User already exists").timestamp(new Date()).build());
    }

    @Override
    public AuthResponse login(String username, String password) {
        var authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        if (authentication.isAuthenticated()) {
            var user = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();
            return AuthResponse.builder()
                    .accessTokenExpiration(jwtProperties.accessTokenExpiration())
                    .refreshTokenExpiration(jwtProperties.refreshTokenExpiration())
                    .accessToken(jwtService.generateAccessToken(user.getUsername(), user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList()))
                    .refreshToken(refreshTokenService.createRefreshToken(user.getUsername()).getToken())
                    .build();
        } else {
            throw new AuthException(ErrorDTO.builder().message("Authentication failed").timestamp(new Date()).build());
        }
    }

    @Override
    public void logout(String refreshToken) {
        refreshTokenService.deleteRefreshToken(refreshToken);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        return refreshTokenService.verifyExpiration(refreshToken)
                .map(RefreshToken::getUser)
                .map(user -> AuthResponse.builder()
                        .accessTokenExpiration(jwtProperties.accessTokenExpiration())
                        .accessToken(jwtService.generateAccessToken(user.getUsername(), user.getRoles().stream().map(Role::getName).toList()))
                        .build())
                .orElseThrow(() -> {
                    refreshTokenService.deleteRefreshToken(refreshToken);
                    return new AuthException(ErrorDTO.builder().message("Invalid refresh token").timestamp(new Date()).build());
                });
    }
}
