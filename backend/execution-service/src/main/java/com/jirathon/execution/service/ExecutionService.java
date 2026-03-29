package com.jirathon.execution.service;

import com.jirathon.execution.config.ExecutionProperties;
import com.jirathon.execution.dto.request.ExecutionRequest;
import com.jirathon.execution.dto.response.ExecutionResponse;
import com.jirathon.execution.model.ExecutionLanguage;
import com.jirathon.execution.sandbox.DockerSandboxStrategy;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Set;

@Service
public class ExecutionService {

    private final ExecutionProperties properties;
    private final DockerSandboxStrategy dockerSandboxStrategy;

    public ExecutionService(ExecutionProperties properties, DockerSandboxStrategy dockerSandboxStrategy) {
        this.properties = properties;
        this.dockerSandboxStrategy = dockerSandboxStrategy;
    }

    public ExecutionResponse execute(ExecutionRequest request) {
        validatePayloadSizes(request);

        ExecutionLanguage language = ExecutionLanguage.fromKey(request.getLanguage());
        long timeLimitMs = request.getTimeLimitMs() == null
                ? properties.getDefaultTimeLimitMs()
                : Math.min(request.getTimeLimitMs(), properties.getMaxTimeLimitMs());
        int memoryLimitMb = request.getMemoryLimitMb() == null
                ? properties.getDefaultMemoryLimitMb()
                : Math.min(request.getMemoryLimitMb(), properties.getMaxMemoryLimitMb());

        return dockerSandboxStrategy.execute(
                language,
                request.getSourceCode(),
                request.getStdin(),
                timeLimitMs,
                memoryLimitMb
        );
    }

    public Set<String> getSupportedLanguages() {
        return Set.of("java", "python", "cpp", "javascript");
    }

    private void validatePayloadSizes(ExecutionRequest request) {
        int codeBytes = request.getSourceCode().getBytes(StandardCharsets.UTF_8).length;
        if (codeBytes > properties.getMaxCodeSizeBytes()) {
            throw new IllegalArgumentException("Source code size exceeds allowed limit");
        }

        String stdin = request.getStdin() == null ? "" : request.getStdin();
        int inputBytes = stdin.getBytes(StandardCharsets.UTF_8).length;
        if (inputBytes > properties.getMaxInputSizeBytes()) {
            throw new IllegalArgumentException("Input size exceeds allowed limit");
        }
    }
}
