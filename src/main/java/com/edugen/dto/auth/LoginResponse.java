package com.edugen.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginResponse {
    private UUID userId;
    private String email;
    private String fullName;
    private String userType;
    private String token;
    private String refreshToken;
    private long expiresIn;
}
