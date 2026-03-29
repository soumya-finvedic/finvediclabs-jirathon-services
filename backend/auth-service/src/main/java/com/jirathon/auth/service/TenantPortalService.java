package com.jirathon.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Locale;
import java.util.Map;

@Service
public class TenantPortalService {

    public static final String DEFAULT_TENANT = "jirathon";

    private static final Map<String, String> TENANT_ALIASES = Map.of(
            "jirathon", "jirathon",
            "scalegrad", "jirathon",
            "hackathon", "hackathon"
    );

    public String resolveTenantId(String rawTenantId) {
        if (!StringUtils.hasText(rawTenantId)) {
            return DEFAULT_TENANT;
        }

        String normalized = rawTenantId.trim().toLowerCase(Locale.ROOT);
        String resolved = TENANT_ALIASES.get(normalized);
        if (resolved == null) {
            throw new IllegalArgumentException("Unsupported tenant: " + rawTenantId);
        }
        return resolved;
    }

    public List<Map<String, String>> getAvailablePortals() {
        return List.of(
                Map.of(
                        "id", "hackathon",
                        "label", "Hackathon login",
                        "description", "Login for hackathon-specific users and organizers"
                ),
                Map.of(
                        "id", "jirathon",
                        "label", "Jirathon login",
                        "description", "Login for the main Jirathon learning and contest platform"
                )
        );
    }
}