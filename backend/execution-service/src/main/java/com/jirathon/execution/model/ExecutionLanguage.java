package com.jirathon.execution.model;

import java.util.Arrays;

public enum ExecutionLanguage {
    JAVA("java"),
    PYTHON("python"),
    CPP("cpp"),
    JAVASCRIPT("javascript");

    private final String key;

    ExecutionLanguage(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public static ExecutionLanguage fromKey(String key) {
        return Arrays.stream(values())
                .filter(lang -> lang.key.equalsIgnoreCase(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported language: " + key));
    }
}
