package com.jirathon.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public class OAuthCallbackRequest {

    @NotBlank(message = "Authorization code is required")
    private String code;

    @NotBlank(message = "Provider is required")
    private String provider; // GOOGLE, AZURE_AD

    private String redirectUri;

    public String getCode() { return code; }
    public String getProvider() { return provider; }
    public String getRedirectUri() { return redirectUri; }

    public void setCode(String code) { this.code = code; }
    public void setProvider(String provider) { this.provider = provider; }
    public void setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; }
}
