package com.jirathon.auth.security;

import com.jirathon.auth.service.TenantPortalService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Extracts tenantId from X-Tenant-Id header (set by API Gateway) or from
 * the JWT token. Runs before JwtAuthenticationFilter for unauthenticated
 * endpoints that still need tenant context (e.g., /auth/register).
 */
@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final String TENANT_HEADER = "X-Tenant-Id";

    private final TenantPortalService tenantPortalService;

    public TenantFilter(TenantPortalService tenantPortalService) {
        this.tenantPortalService = tenantPortalService;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String tenantId;

        try {
            tenantId = tenantPortalService.resolveTenantId(request.getHeader(TENANT_HEADER));
        } catch (IllegalArgumentException ex) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
            return;
        }

        if (StringUtils.hasText(tenantId) && TenantContext.getTenantId() == null) {
            TenantContext.setTenantId(tenantId.trim());
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // Only clear if not already managed by JwtAuthenticationFilter
            if (org.springframework.security.core.context.SecurityContextHolder
                    .getContext().getAuthentication() == null) {
                TenantContext.clear();
            }
        }
    }
}
