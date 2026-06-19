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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import com.trycore.evmTracker.presentation.exception.ApiError;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Authentication", description = "Endpoints for user registration, login and token refresh")
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

    @Operation(summary = "Login", description = "Authenticate user and return JWT bearer token with refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Authentication successful", content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Bad request or validation error", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Invalid username or password", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
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

    @Operation(summary = "Register", description = "Create a new user account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User registered successfully", content = @Content(schema = @Schema(type = "string"))),
            @ApiResponse(responseCode = "400", description = "Invalid request payload or username already exists", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        userService.register(request.username(), request.password(), Set.of(Role.USER));
        return ResponseEntity.ok("Usuario registrado correctamente");
    }

    @Operation(summary = "Refresh token", description = "Exchange a valid refresh token for a new JWT access token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "New access token returned", content = @Content(schema = @Schema(implementation = AuthenticationResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token request", content = @Content(schema = @Schema(implementation = ApiError.class))),
            @ApiResponse(responseCode = "401", description = "Refresh token expired or invalid", content = @Content(schema = @Schema(implementation = ApiError.class)))
    })
    @PostMapping("/refresh")
    public AuthenticationResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        var refreshToken = refreshTokenService.verifyRefreshToken(request.refreshToken());
        String token = jwtService.generateToken(refreshToken.getUser().getUsername());
        return new AuthenticationResponse(token, refreshToken.getToken());
    }
}
