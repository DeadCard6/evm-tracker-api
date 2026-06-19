package com.trycore.evmTracker.presentation.controller;

import com.trycore.evmTracker.application.service.RefreshTokenService;
import com.trycore.evmTracker.application.service.UserService;
import com.trycore.evmTracker.domain.model.Role;
import com.trycore.evmTracker.domain.model.User;
import com.trycore.evmTracker.infrastructure.security.JwtService;
import com.trycore.evmTracker.presentation.dto.AuthenticationRequest;
import com.trycore.evmTracker.presentation.dto.AuthenticationResponse;
import com.trycore.evmTracker.presentation.dto.RefreshTokenRequest;
import com.trycore.evmTracker.presentation.dto.RegisterRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtService jwtService,
                          UserService userService,
                          RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = userService.findByUsername(authentication.getName());
        var refreshToken = refreshTokenService.createRefreshToken(user);
        String token = jwtService.generateToken(authentication.getName());
        return new AuthenticationResponse(token, refreshToken.getToken());
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.register(request.username(), request.password(), Set.of(Role.USER));
        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @PostMapping("/refresh")
    public AuthenticationResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        var refreshToken = refreshTokenService.verifyRefreshToken(request.refreshToken());
        String token = jwtService.generateToken(refreshToken.getUser().getUsername());
        return new AuthenticationResponse(token, refreshToken.getToken());
    }
}
