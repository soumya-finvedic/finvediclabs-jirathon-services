package com.jirathon.auth.service;

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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class OAuthService {

    private static final Logger log = LoggerFactory.getLogger(OAuthService.class);

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final RestTemplate restTemplate;

    // Google OAuth
    @Value("${oauth.google.client-id:}")
    private String googleClientId;
    @Value("${oauth.google.client-secret:}")
    private String googleClientSecret;
    @Value("${oauth.google.redirect-uri:}")
    private String googleRedirectUri;

    // Azure AD OAuth
    @Value("${oauth.azure.client-id:}")
    private String azureClientId;
    @Value("${oauth.azure.client-secret:}")
    private String azureClientSecret;
    @Value("${oauth.azure.redirect-uri:}")
    private String azureRedirectUri;
    @Value("${oauth.azure.tenant-id:common}")
    private String azureTenantId;

    public OAuthService(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
                        RefreshTokenService refreshTokenService) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.restTemplate = new RestTemplate();
    }

    public String getGoogleAuthUrl(String tenantId) {
        return "https://accounts.google.com/o/oauth2/v2/auth"
                + "?client_id=" + googleClientId
                + "&redirect_uri=" + googleRedirectUri
                + "&response_type=code"
                + "&scope=openid%20email%20profile"
                + "&state=" + tenantId
                + "&access_type=offline"
                + "&prompt=consent";
    }

    public String getAzureAuthUrl(String tenantId) {
        return "https://login.microsoftonline.com/" + azureTenantId + "/oauth2/v2.0/authorize"
                + "?client_id=" + azureClientId
                + "&redirect_uri=" + azureRedirectUri
                + "&response_type=code"
                + "&scope=openid%20email%20profile"
                + "&state=" + tenantId
                + "&response_mode=query";
    }

    @SuppressWarnings("unchecked")
    public AuthResponse handleGoogleCallback(String code, String tenantId, String deviceInfo, String ipAddress) {
        // Exchange code for tokens
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", googleClientId);
        params.add("client_secret", googleClientSecret);
        params.add("redirect_uri", googleRedirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                Map.class
        );

        Map<String, Object> tokenBody = tokenResponse.getBody();
        if (tokenBody == null) {
            throw new AuthException("Google token response was empty");
        }

        String accessToken = (String) tokenBody.get("access_token");

        // Fetch user info
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.setBearerAuth(accessToken);

        ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                new HttpEntity<>(userInfoHeaders),
                Map.class
        );

        Map<String, Object> userInfo = userInfoResponse.getBody();
        if (userInfo == null) {
            throw new AuthException("Google user info response was empty");
        }
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");
        String picture = (String) userInfo.get("picture");
        String googleId = (String) userInfo.get("id");

        return upsertOAuthUser(tenantId, email, name, picture, OAuthProvider.GOOGLE, googleId, deviceInfo, ipAddress);
    }

    @SuppressWarnings("unchecked")
    public AuthResponse handleAzureCallback(String code, String tenantId, String deviceInfo, String ipAddress) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", azureClientId);
        params.add("client_secret", azureClientSecret);
        params.add("redirect_uri", azureRedirectUri);
        params.add("grant_type", "authorization_code");
        params.add("scope", "openid email profile");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        ResponseEntity<Map> tokenResponse = restTemplate.exchange(
                "https://login.microsoftonline.com/" + azureTenantId + "/oauth2/v2.0/token",
                HttpMethod.POST,
                new HttpEntity<>(params, headers),
                Map.class
        );

        Map<String, Object> tokenBody = tokenResponse.getBody();
        if (tokenBody == null) {
            throw new AuthException("Microsoft token response was empty");
        }

        String msAccessToken = (String) tokenBody.get("access_token");

        // Fetch user profile from MS Graph
        HttpHeaders graphHeaders = new HttpHeaders();
        graphHeaders.setBearerAuth(msAccessToken);

        ResponseEntity<Map> profileResponse = restTemplate.exchange(
                "https://graph.microsoft.com/v1.0/me",
                HttpMethod.GET,
                new HttpEntity<>(graphHeaders),
                Map.class
        );

        Map<String, Object> profile = profileResponse.getBody();
        if (profile == null) {
            throw new AuthException("Microsoft profile response was empty");
        }
        String email = (String) profile.get("mail");
        if (email == null) {
            email = (String) profile.get("userPrincipalName");
        }
        String name = (String) profile.get("displayName");
        String azureId = (String) profile.get("id");

        return upsertOAuthUser(tenantId, email, name, null, OAuthProvider.AZURE_AD, azureId, deviceInfo, ipAddress);
    }

    private AuthResponse upsertOAuthUser(String tenantId, String email, String displayName,
                                          String avatarUrl, OAuthProvider provider, String providerId,
                                          String deviceInfo, String ipAddress) {
        User user = userRepository
                .findByTenantIdAndOauthProviderAndOauthProviderIdAndDeletedFalse(tenantId, provider, providerId)
                .orElseGet(() -> userRepository.findByTenantIdAndEmailAndDeletedFalse(tenantId, email)
                        .orElse(null));

        if (user == null) {
            // Create new user
            user = User.builder()
                    .tenantId(tenantId)
                    .email(email)
                    .displayName(displayName)
                    .avatarUrl(avatarUrl)
                    .oauthProvider(provider)
                    .oauthProviderId(providerId)
                    .roles(Set.of(Role.USER))
                    .status(UserStatus.ACTIVE)
                    .emailVerified(true) // OAuth providers verify email
                    .lastLoginAt(Instant.now())
                    .build();
            user = userRepository.save(user);
            log.info("Created OAuth user: {} via {} for tenant {}", email, provider, tenantId);
        } else {
            // Update existing user with OAuth info if not set
            if (user.getOauthProvider() == null) {
                user.setOauthProvider(provider);
                user.setOauthProviderId(providerId);
            }
            if (avatarUrl != null && user.getAvatarUrl() == null) {
                user.setAvatarUrl(avatarUrl);
            }
            user.setEmailVerified(true);
            user.setStatus(UserStatus.ACTIVE);
            user.setLastLoginAt(Instant.now());
            user = userRepository.save(user);
        }

        if (user.getStatus() == UserStatus.BANNED) {
            throw new AuthException("Account has been suspended");
        }

        return buildAuthResponse(user, deviceInfo, ipAddress);
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
