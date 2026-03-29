package com.jirathon.execution.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
public class ExecutionRequest {

    @NotBlank(message = "Language is required")
    @Pattern(regexp = "^(java|python|cpp|javascript)$", flags = Pattern.Flag.CASE_INSENSITIVE,
            message = "Language must be one of java, python, cpp, javascript")
    private String language;

    @NotBlank(message = "Source code is required")
    @Size(max = 100000, message = "Source code exceeds maximum allowed size")
    private String sourceCode;

    @Size(max = 20000, message = "Input exceeds maximum allowed size")
    private String stdin;

    @Min(value = 100, message = "Time limit must be at least 100ms")
    @Max(value = 10000, message = "Time limit must be at most 10000ms")
    private Long timeLimitMs;

    @Min(value = 32, message = "Memory limit must be at least 32MB")
    @Max(value = 512, message = "Memory limit must be at most 512MB")
    private Integer memoryLimitMb;

    public ExecutionRequest() {
    }

    public ExecutionRequest(String language, String sourceCode, String stdin, Long timeLimitMs, Integer memoryLimitMb) {
        this.language = language;
        this.sourceCode = sourceCode;
        this.stdin = stdin;
        this.timeLimitMs = timeLimitMs;
        this.memoryLimitMb = memoryLimitMb;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getSourceCode() {
        return sourceCode;
    }

    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    public String getStdin() {
        return stdin;
    }

    public void setStdin(String stdin) {
        this.stdin = stdin;
    }

    public Long getTimeLimitMs() {
        return timeLimitMs;
    }

    public void setTimeLimitMs(Long timeLimitMs) {
        this.timeLimitMs = timeLimitMs;
    }

    public Integer getMemoryLimitMb() {
        return memoryLimitMb;
    }

    public void setMemoryLimitMb(Integer memoryLimitMb) {
        this.memoryLimitMb = memoryLimitMb;
    }
}
