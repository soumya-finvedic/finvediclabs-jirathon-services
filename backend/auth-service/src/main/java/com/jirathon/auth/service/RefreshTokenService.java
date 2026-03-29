package com.jirathon.auth.service;

import com.jirathon.auth.model.RefreshToken;
import com.jirathon.auth.repository.RefreshTokenRepository;
import com.jirathon.auth.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class RefreshTokenService {

    private static final int MAX_ACTIVE_SESSIONS = 5;

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               JwtTokenProvider jwtTokenProvider,
                               PasswordEncoder passwordEncoder) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
    }

    public String createRefreshToken(String userId, String tenantId, String deviceInfo, String ipAddress) {
        // Enforce max active sessions
        long activeCount = refreshTokenRepository.countByUserIdAndTenantIdAndRevokedFalse(userId, tenantId);
        if (activeCount >= MAX_ACTIVE_SESSIONS) {
            refreshTokenRepository.deleteAllByUserIdAndTenantId(userId, tenantId);
        }

        String rawToken = jwtTokenProvider.generateRefreshToken(userId, tenantId);

        RefreshToken refreshToken = RefreshToken.builder()
                .tenantId(tenantId)
                .userId(userId)
                .tokenHash(passwordEncoder.encode(rawToken))
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .expiresAt(Instant.now().plusMillis(jwtTokenProvider.getRefreshTokenExpirationMs()))
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);
        return rawToken;
    }

    public RefreshToken validateRefreshToken(String rawToken) {
        // Since tokens are hashed, we need to iterate. For production scale,
        // store a token ID (jti) alongside the hash and look up by jti.
        // For now, validate the JWT signature first, then find by user.
        if (!jwtTokenProvider.validateToken(rawToken)) {
            return null;
        }

        String userId = jwtTokenProvider.getUserIdFromToken(rawToken);
        String tenantId = jwtTokenProvider.getTenantIdFromToken(rawToken);

        // Find the non-revoked token for this user.
        // In production: store jti claim in the RefreshToken document and index on it.
        return refreshTokenRepository.findByTokenHashAndRevokedFalse(
                passwordEncoder.encode(rawToken)
        ).orElse(null);
    }

    public void revokeAllUserTokens(String userId, String tenantId) {
        refreshTokenRepository.deleteAllByUserIdAndTenantId(userId, tenantId);
    }
}
