package com.jirathon.auth.dto.response;

public class OAuthUrlResponse {

    private String provider;
    private String authorizationUrl;

    public OAuthUrlResponse() {
    }

    public OAuthUrlResponse(String provider, String authorizationUrl) {
        this.provider = provider;
        this.authorizationUrl = authorizationUrl;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String provider;
        private String authorizationUrl;

        public Builder provider(String provider) { this.provider = provider; return this; }
        public Builder authorizationUrl(String authorizationUrl) { this.authorizationUrl = authorizationUrl; return this; }

        public OAuthUrlResponse build() {
            return new OAuthUrlResponse(provider, authorizationUrl);
        }
    }

    public String getProvider() { return provider; }
    public String getAuthorizationUrl() { return authorizationUrl; }

    public void setProvider(String provider) { this.provider = provider; }
    public void setAuthorizationUrl(String authorizationUrl) { this.authorizationUrl = authorizationUrl; }
}
