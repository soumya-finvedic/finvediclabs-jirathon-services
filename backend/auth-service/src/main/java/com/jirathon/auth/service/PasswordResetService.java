package com.jirathon.auth.service;

import com.jirathon.auth.exception.AuthException;
import com.jirathon.auth.model.PasswordResetToken;
import com.jirathon.auth.model.User;
import com.jirathon.auth.repository.PasswordResetTokenRepository;
import com.jirathon.auth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;

@Service
public class PasswordResetService {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);
    private static final int TOKEN_BYTE_LENGTH = 32;
    private static final int TOKEN_EXPIRY_HOURS = 1;

    private final PasswordResetTokenRepository resetTokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final String frontendBaseUrl;

    public PasswordResetService(
            PasswordResetTokenRepository resetTokenRepository,
            UserRepository userRepository,
            EmailService emailService,
            PasswordEncoder passwordEncoder,
            RefreshTokenService refreshTokenService,
            @Value("${app.frontend-base-url:https://app.jirathon.io}") String frontendBaseUrl
    ) {
        this.resetTokenRepository = resetTokenRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
        this.refreshTokenService = refreshTokenService;
        this.frontendBaseUrl = frontendBaseUrl;
    }

    public void initiatePasswordReset(String tenantId, String email) {
        User user = userRepository.findByTenantIdAndEmailAndDeletedFalse(tenantId, email)
                .orElseThrow(() -> {
                    log.warn("❌ Password reset requested for non-existent email: {}", email);
                    return new AuthException("The email entered is not registered. Please enter registered email");
                });

        try {
            // Invalidate existing reset tokens
            resetTokenRepository.deleteAllByTenantIdAndUserId(tenantId, user.getId());
            
            String rawToken = generateSecureToken();
            
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .tenantId(tenantId)
                    .userId(user.getId())
                    .token(rawToken)  // Store raw token for lookup
                    .tokenHash(passwordEncoder.encode(rawToken))  // Also store hash for security
                    .expiresAt(Instant.now().plus(TOKEN_EXPIRY_HOURS, ChronoUnit.HOURS))
                    .used(false)
                    .build();

            resetTokenRepository.save(resetToken);
            
            String resetLink = frontendBaseUrl + "/reset-password?token=" + rawToken;
            emailService.sendPasswordResetEmail(email, resetLink);
            
            log.info("✅ Password reset initiated for user: {} | Token expires in {} hours", email, TOKEN_EXPIRY_HOURS);
        } catch (Exception e) {
            log.error("❌ Failed to initiate password reset for email: {}", email, e);
            // Don't throw - still return success to prevent email enumeration
        }
    }

    public void resetPassword(String tenantId, String rawToken, String newPassword) {
        log.info("=== STARTING PASSWORD RESET ===");
        
        // ✅ FIX: Look up token by raw token value (not re-encoded)
        PasswordResetToken resetToken = resetTokenRepository
                .findByTokenAndUsedFalse(rawToken)
                .orElseThrow(() -> {
                    log.warn("❌ Reset token not found or already used");
                    return new AuthException("Invalid or expired reset token");
                });
        
        log.info("✅ Reset token found for user: {}", resetToken.getUserId());

        if (resetToken.isExpired()) {
            log.warn("❌ Reset token has expired: {}", resetToken.getId());
            throw new AuthException("Reset token has expired");
        }

        if (!resetToken.getTenantId().equals(tenantId)) {
            log.warn("❌ Token tenant mismatch. Expected: {}, Got: {}", tenantId, resetToken.getTenantId());
            throw new AuthException("Invalid reset token for this tenant");
        }

        User user = userRepository.findByIdAndTenantIdAndDeletedFalse(resetToken.getUserId(), tenantId)
                .orElseThrow(() -> {
                    log.error("❌ User not found for reset token");
                    return new AuthException("User not found");
                });

        log.info("🔄 Updating password for user: {}", user.getEmail());
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("✅ Password updated successfully");

        // Mark token as used
        resetToken.setUsed(true);
        resetTokenRepository.save(resetToken);
        log.info("✅ Reset token marked as used");

        // Revoke all refresh tokens (force re-login on all devices)
        refreshTokenService.revokeAllUserTokens(user.getId(), tenantId);
        log.info("✅ All refresh tokens revoked for user");
        
        // Send password change confirmation email
        try {
            emailService.sendPasswordChangeConfirmationEmail(user.getEmail(), user.getDisplayName());
            log.info("✅ Password change confirmation email sent to: {}", user.getEmail());
        } catch (Exception e) {
            log.error("⚠️ Failed to send password change confirmation email: {}", e.getMessage());
            // Don't fail the password reset if email send fails
        }
        
        log.info("=== PASSWORD RESET COMPLETED SUCCESSFULLY ===");
    }

    private String generateSecureToken() {
        byte[] tokenBytes = new byte[TOKEN_BYTE_LENGTH];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
}
