package com.jirathon.auth.service;

import com.jirathon.auth.model.OtpToken;
import com.jirathon.auth.repository.OtpTokenRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class OtpService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 10;

    private final OtpTokenRepository otpTokenRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(OtpTokenRepository otpTokenRepository,
                      EmailService emailService,
                      PasswordEncoder passwordEncoder) {
        this.otpTokenRepository = otpTokenRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public void generateAndSendOtp(String tenantId, String email, String purpose) {
        // Invalidate any existing OTP for this email + purpose
        otpTokenRepository.deleteAllByTenantIdAndEmailAndPurpose(tenantId, email, purpose);

        String otp = generateOtp();

        OtpToken otpToken = OtpToken.builder()
                .tenantId(tenantId)
                .email(email)
                .otpHash(passwordEncoder.encode(otp))
                .purpose(purpose)
                .attemptCount(0)
                .expiresAt(Instant.now().plus(OTP_EXPIRY_MINUTES, ChronoUnit.MINUTES))
                .used(false)
                .build();

        otpTokenRepository.save(otpToken);
        emailService.sendOtpEmail(email, otp, purpose);
    }

    public boolean verifyOtp(String tenantId, String email, String otp, String purpose) {
        OtpToken otpToken = otpTokenRepository
                .findFirstByTenantIdAndEmailAndPurposeAndUsedFalseOrderByCreatedAtDesc(tenantId, email, purpose)
                .orElse(null);

        if (otpToken == null || otpToken.isExpired() || otpToken.hasExceededAttempts()) {
            return false;
        }

        // Increment attempt count regardless of outcome
        otpToken.setAttemptCount(otpToken.getAttemptCount() + 1);

        if (passwordEncoder.matches(otp, otpToken.getOtpHash())) {
            otpToken.setUsed(true);
            otpTokenRepository.save(otpToken);
            return true;
        }

        otpTokenRepository.save(otpToken);
        return false;
    }

    private String generateOtp() {
        int bound = (int) Math.pow(10, OTP_LENGTH);
        int number = secureRandom.nextInt(bound);
        return String.format("%0" + OTP_LENGTH + "d", number);
    }
}
