package com.edugen.service;

import com.edugen.dto.auth.*;
import com.edugen.entity.*;
import com.edugen.exception.BadRequestException;
import com.edugen.exception.ConflictException;
import com.edugen.exception.ResourceNotFoundException;
import com.edugen.exception.UnauthorizedException;
import com.edugen.repository.*;
import com.edugen.security.JwtTokenProvider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private TeacherProfileRepository teacherProfileRepository;

    @Autowired
    private AdminProfileRepository adminProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.expiration}")
    private long tokenExpirationMs;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new UnauthorizedException("Invalid email or password");
        }

        if (!user.getIsActive()) {
            throw new UnauthorizedException("User account is inactive");
        }

        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail(), user.getUserType().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        return LoginResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userType(user.getUserType().toString())
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(tokenExpirationMs / 1000)
                .build();
    }

    public LoginResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email already exists");
        }

        School school = schoolRepository.findById(UUID.fromString(request.getSchoolId()))
                .orElseThrow(() -> new ResourceNotFoundException("School not found"));

        User.UserType userType = User.UserType.valueOf(request.getUserType().toUpperCase());

        User user = User.builder()
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .userType(userType)
                .isActive(true)
                .build();

        user = userRepository.save(user);

        // Create profile based on user type
        if (userType == User.UserType.STUDENT) {
            if (request.getParentEmail() == null || request.getClassSection() == null) {
                throw new BadRequestException("Parent email and class section are required for students");
            }

            StudentProfile profile = StudentProfile.builder()
                    .user(user)
                    .parentEmail(request.getParentEmail())
                    .classSection(request.getClassSection())
                    .rollNumber(request.getRollNumber())
                    .school(school)
                    .currentCGPA(java.math.BigDecimal.ZERO)
                    .totalExamsTaken(0)
                    .build();

            studentProfileRepository.save(profile);
        } else if (userType == User.UserType.TEACHER) {
            if (request.getSubject() == null) {
                throw new BadRequestException("Subject is required for teachers");
            }

            TeacherProfile profile = TeacherProfile.builder()
                    .user(user)
                    .subject(request.getSubject())
                    .department(request.getDepartment())
                    .school(school)
                    .totalExamsCreated(0)
                    .build();

            teacherProfileRepository.save(profile);
        }

        String token = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail(), user.getUserType().toString());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getUserId());

        return LoginResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .userType(user.getUserType().toString())
                .token(token)
                .refreshToken(refreshToken)
                .expiresIn(tokenExpirationMs / 1000)
                .build();
    }

    public RefreshTokenResponse refreshToken(RefreshTokenRequest request) {
        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            throw new UnauthorizedException("Refresh token is invalid or expired");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String newToken = jwtTokenProvider.generateToken(user.getUserId(), user.getEmail(), user.getUserType().toString());

        return RefreshTokenResponse.builder()
                .token(newToken)
                .expiresIn(tokenExpirationMs / 1000)
                .build();
    }

    public void logout(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // Token invalidation would typically be done via a blacklist (Redis cache)
        log.info("User {} logged out", userId);
    }

    public void requestPasswordReset(PasswordResetRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // In real implementation, generate reset token and send email
        log.info("Password reset requested for user: {}", request.getEmail());
    }

    public void resetPassword(String resetToken, String newPassword) {
        if (!jwtTokenProvider.validateToken(resetToken)) {
            throw new UnauthorizedException("Reset token is invalid or expired");
        }

        UUID userId = jwtTokenProvider.getUserIdFromToken(resetToken);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password reset for user: {}", user.getEmail());
    }
}
