package com.jirathon.auth.service;

import com.jirathon.auth.dto.request.LoginRequest;
import com.jirathon.auth.dto.request.RegisterRequest;
import com.jirathon.auth.dto.response.AuthResponse;
import com.jirathon.auth.exception.AuthException;
import com.jirathon.auth.model.User;
import com.jirathon.auth.model.enums.OAuthProvider;
import com.jirathon.auth.model.enums.Role;
import com.jirathon.auth.model.enums.UserStatus;
import com.jirathon.auth.repository.UserRepository;
import com.jirathon.auth.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final OtpService otpService;
    private final EmailService emailService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider, RefreshTokenService refreshTokenService,
                       OtpService otpService, EmailService emailService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.otpService = otpService;
        this.emailService = emailService;
    }

    public AuthResponse register(RegisterRequest request, String tenantId) {
        if (userRepository.existsByTenantIdAndEmailAndDeletedFalse(tenantId, request.getEmail())) {
            throw new AuthException("An account with this email already exists");
        }

        if (request.getUsername() != null && !request.getUsername().isBlank() &&
                userRepository.existsByTenantIdAndUsernameAndDeletedFalse(tenantId, request.getUsername())) {
            throw new AuthException("Username is already taken");
        }

        User user = User.builder()
                .tenantId(tenantId)
                .email(request.getEmail())
                .username(request.getUsername())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .displayName(request.getUsername())
                .oauthProvider(OAuthProvider.LOCAL)
                .roles(Set.of(Role.USER))
                .status(UserStatus.PENDING_VERIFICATION)
                .emailVerified(false)
                .build();

        user = userRepository.save(user);
        log.info("User registered: {} in tenant {}", user.getEmail(), tenantId);

        // Send email verification OTP
        otpService.generateAndSendOtp(tenantId, user.getEmail(), "EMAIL_VERIFICATION");

        // Return tokens even before verification (limited access until verified)
        return buildAuthResponse(user, null, null);
    }

    public AuthResponse login(LoginRequest request, String tenantId, String deviceInfo, String ipAddress) {
        User user = userRepository.findByTenantIdAndEmailAndDeletedFalse(tenantId, request.getEmail())
                .orElseThrow(() -> new AuthException("Invalid email or password"));

        if (user.getOauthProvider() != OAuthProvider.LOCAL && user.getPasswordHash() == null) {
            throw new AuthException(
                    "This account uses " + user.getOauthProvider().name() + " sign-in. Please use OAuth login."
            );
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AuthException("Invalid email or password");
        }

        if (user.getStatus() == UserStatus.BANNED) {
            throw new AuthException("Account has been suspended");
        }

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return buildAuthResponse(user, deviceInfo, ipAddress);
    }

    public AuthResponse refreshAccessToken(String refreshTokenRaw, String deviceInfo, String ipAddress) {
        if (!jwtTokenProvider.validateToken(refreshTokenRaw)) {
            throw new AuthException("Invalid or expired refresh token");
        }

        String userId = jwtTokenProvider.getUserIdFromToken(refreshTokenRaw);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(refreshTokenRaw);

        User user = userRepository.findByIdAndTenantIdAndDeletedFalse(userId, tenantId)
                .orElseThrow(() -> new AuthException("User not found"));

        if (user.getStatus() == UserStatus.BANNED) {
            throw new AuthException("Account has been suspended");
        }

        // Revoke old refresh token and issue new pair (token rotation)
        refreshTokenService.revokeAllUserTokens(userId, tenantId);

        return buildAuthResponse(user, deviceInfo, ipAddress);
    }

    @Transactional
    public User verifyEmail(String tenantId, String email, String otp) {
        log.info("=== STARTING EMAIL VERIFICATION ===");
        log.info("Tenant: {}, Email: {}", tenantId, email);
        
        boolean valid = otpService.verifyOtp(tenantId, email, otp, "EMAIL_VERIFICATION");
        log.info("OTP validation result: {} for email: {}", valid, email);
        
        if (!valid) {
            log.warn("OTP verification failed for email: {}", email);
            throw new AuthException("Invalid or expired OTP");
        }

        User user = userRepository.findByTenantIdAndEmailAndDeletedFalse(tenantId, email)
                .orElseThrow(() -> {
                    log.error("User not found: {} in tenant {}", email, tenantId);
                    return new AuthException("User not found");
                });

        log.info("User found: {} | Current status: {} | Current emailVerified: {}", 
                 email, user.getStatus(), user.isEmailVerified());
        
        // Update user verification status
        user.setEmailVerified(true);
        if (user.getStatus() == UserStatus.PENDING_VERIFICATION) {
            user.setStatus(UserStatus.ACTIVE);
            log.info("Status updated from PENDING_VERIFICATION to ACTIVE for: {}", email);
        }
        
        // Save to database
        user = userRepository.save(user);
        log.info("✅ User saved to database - Status: {} | emailVerified: {} | Id: {}", 
                 user.getStatus(), user.isEmailVerified(), user.getId());
        
        // Force read from database to verify changes persisted
        User verifyUser = userRepository.findByIdAndTenantIdAndDeletedFalse(user.getId(), tenantId)
                .orElseThrow(() -> {
                    log.error("Failed to verify saved user from DB: {} in tenant {}", email, tenantId);
                    return new AuthException("Failed to verify saved user");
                });
        
        log.info("✅ VERIFICATION FROM DB - User: {} | Status: {} | emailVerified: {} | Id: {}", 
                 email, verifyUser.getStatus(), verifyUser.isEmailVerified(), verifyUser.getId());
        
        if (!verifyUser.isEmailVerified() || verifyUser.getStatus() != UserStatus.ACTIVE) {
            log.error("❌ DATABASE PERSISTENCE FAILED: Email verified={}, Status={}",
                    verifyUser.isEmailVerified(), verifyUser.getStatus());
            throw new AuthException("Email verification failed: database update did not persist");
        }
        
        log.info("=== EMAIL VERIFICATION COMPLETED SUCCESSFULLY ===");
        
        // Send verification confirmation email
        try {
            emailService.sendVerificationConfirmationEmail(user.getEmail(), user.getDisplayName());
            log.info("✅ Verification confirmation email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("⚠️ Failed to send verification confirmation email to {}: {}", 
                      user.getEmail(), e.getMessage());
            // Don't fail the verification if email send fails - the user is already verified
        }
        
        return user;
    }

    public void resendOtp(String tenantId, String email, String purpose) {
        User user = userRepository.findByTenantIdAndEmailAndDeletedFalse(tenantId, email)
                .orElseThrow(() -> new AuthException("User not found"));

        if ("EMAIL_VERIFICATION".equals(purpose) && user.isEmailVerified()) {
            throw new AuthException("Email is already verified");
        }

        otpService.generateAndSendOtp(tenantId, email, purpose);
    }

    public void logout(String userId, String tenantId) {
        refreshTokenService.revokeAllUserTokens(userId, tenantId);
        log.info("User logged out: {} from tenant {}", userId, tenantId);
    }

    private AuthResponse buildAuthResponse(User user, String deviceInfo, String ipAddress) {
        Set<String> roleNames = user.getRoles().stream().map(Enum::name).collect(Collectors.toSet());

        String accessToken = jwtTokenProvider.generateAccessToken(
                user.getId(), user.getTenantId(), user.getEmail(), roleNames
        );
        String refreshToken = refreshTokenService.createRefreshToken(
                user.getId(), user.getTenantId(), deviceInfo, ipAddress
        );

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getAccessTokenExpirationMs() / 1000)
                .user(AuthResponse.UserInfo.builder()
                        .id(user.getId())
                        .tenantId(user.getTenantId())
                        .email(user.getEmail())
                        .displayName(user.getDisplayName())
                        .avatarUrl(user.getAvatarUrl())
                        .roles(roleNames)
                        .emailVerified(user.isEmailVerified())
                        .build())
                .build();
    }
}
