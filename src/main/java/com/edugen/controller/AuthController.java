package com.edugen.controller;

import com.edugen.dto.auth.*;
import com.edugen.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "Authentication APIs")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register new user (student or teacher)")
    public ResponseEntity<LoginResponse> register(@Valid @RequestBody RegisterRequest request) {
        log.info("Registering user with email: {}", request.getEmail());
        LoginResponse response = authService.register(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "User login with email and password")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        log.info("User login attempt with email: {}", request.getEmail());
        LoginResponse response = authService.login(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh JWT token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        log.info("Refreshing token");
        RefreshTokenResponse response = authService.refreshToken(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout user")
    public ResponseEntity<Map<String, String>> logout(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getPrincipal().toString());
        log.info("User {} logging out", userId);
        authService.logout(userId);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Successfully logged out");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/password-reset")
    @Operation(summary = "Request password reset")
    public ResponseEntity<Map<String, String>> requestPasswordReset(@Valid @RequestBody PasswordResetRequest request) {
        log.info("Password reset request for email: {}", request.getEmail());
        authService.requestPasswordReset(request);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset link sent to your email");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Complete password reset")
    public ResponseEntity<Map<String, String>> resetPassword(
            @RequestParam String token,
            @RequestParam String newPassword) {
        log.info("Completing password reset");
        authService.resetPassword(token, newPassword);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset successfully");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
