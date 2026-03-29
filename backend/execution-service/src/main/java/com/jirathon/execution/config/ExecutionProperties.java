package com.jirathon.execution.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.LinkedHashMap;
import java.util.Map;

@ConfigurationProperties(prefix = "execution")
public class ExecutionProperties {

    private boolean dockerEnabled = true;
    private long requestTimeoutMs = 10000;
    private long defaultTimeLimitMs = 2000;
    private long maxTimeLimitMs = 10000;
    private int defaultMemoryLimitMb = 128;
    private int maxMemoryLimitMb = 512;
    private int maxCodeSizeBytes = 100000;
    private int maxInputSizeBytes = 20000;
    private int maxOutputSizeBytes = 65536;
    private Map<String, String> languages = new LinkedHashMap<>();

    public boolean isDockerEnabled() {
        return dockerEnabled;
    }

    public void setDockerEnabled(boolean dockerEnabled) {
        this.dockerEnabled = dockerEnabled;
    }

    public long getRequestTimeoutMs() {
        return requestTimeoutMs;
    }

    public void setRequestTimeoutMs(long requestTimeoutMs) {
        this.requestTimeoutMs = requestTimeoutMs;
    }

    public long getDefaultTimeLimitMs() {
        return defaultTimeLimitMs;
    }

    public void setDefaultTimeLimitMs(long defaultTimeLimitMs) {
        this.defaultTimeLimitMs = defaultTimeLimitMs;
    }

    public long getMaxTimeLimitMs() {
        return maxTimeLimitMs;
    }

    public void setMaxTimeLimitMs(long maxTimeLimitMs) {
        this.maxTimeLimitMs = maxTimeLimitMs;
    }

    public int getDefaultMemoryLimitMb() {
        return defaultMemoryLimitMb;
    }

    public void setDefaultMemoryLimitMb(int defaultMemoryLimitMb) {
        this.defaultMemoryLimitMb = defaultMemoryLimitMb;
    }

    public int getMaxMemoryLimitMb() {
        return maxMemoryLimitMb;
    }

    public void setMaxMemoryLimitMb(int maxMemoryLimitMb) {
        this.maxMemoryLimitMb = maxMemoryLimitMb;
    }

    public int getMaxCodeSizeBytes() {
        return maxCodeSizeBytes;
    }

    public void setMaxCodeSizeBytes(int maxCodeSizeBytes) {
        this.maxCodeSizeBytes = maxCodeSizeBytes;
    }

    public int getMaxInputSizeBytes() {
        return maxInputSizeBytes;
    }

    public void setMaxInputSizeBytes(int maxInputSizeBytes) {
        this.maxInputSizeBytes = maxInputSizeBytes;
    }

    public int getMaxOutputSizeBytes() {
        return maxOutputSizeBytes;
    }

    public void setMaxOutputSizeBytes(int maxOutputSizeBytes) {
        this.maxOutputSizeBytes = maxOutputSizeBytes;
    }

    public Map<String, String> getLanguages() {
        return languages;
    }

    public void setLanguages(Map<String, String> languages) {
        this.languages = languages;
    }
}
