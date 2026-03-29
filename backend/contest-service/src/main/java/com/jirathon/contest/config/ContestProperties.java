package com.jirathon.contest.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashSet;
import java.util.Set;

@ConfigurationProperties(prefix = "contest")
public class ContestProperties {

    private Set<String> supportedLanguages = new LinkedHashSet<>();

    public Set<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    public void setSupportedLanguages(Set<String> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }
}
