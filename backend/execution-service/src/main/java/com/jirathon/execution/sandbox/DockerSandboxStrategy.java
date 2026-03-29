package com.jirathon.execution.sandbox;

import com.jirathon.execution.config.ExecutionProperties;
import com.jirathon.execution.dto.response.ExecutionResponse;
import com.jirathon.execution.model.ExecutionLanguage;
import com.jirathon.execution.model.ExecutionStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
public class DockerSandboxStrategy {

    private final ExecutionProperties properties;

    public DockerSandboxStrategy(ExecutionProperties properties) {
        this.properties = properties;
    }

    public ExecutionResponse execute(ExecutionLanguage language, String sourceCode, String stdin,
                                     long timeLimitMs, int memoryLimitMb) {
        if (!properties.isDockerEnabled()) {
            throw new IllegalArgumentException("Docker execution is disabled");
        }

        Path workDir = null;
        long started = System.nanoTime();
        try {
            workDir = Files.createTempDirectory("exec-");
            SandboxSpec spec = buildSpec(language);
            Files.writeString(workDir.resolve(spec.sourceFileName()), sourceCode, StandardCharsets.UTF_8);

            List<String> command = buildDockerCommand(spec, workDir, memoryLimitMb);
            Process process = new ProcessBuilder(command).start();

            try (var stdinStream = process.getOutputStream()) {
                String safeInput = stdin == null ? "" : stdin;
                stdinStream.write(safeInput.getBytes(StandardCharsets.UTF_8));
                stdinStream.flush();
            }

            StreamResult stdout;
            StreamResult stderr;

                ExecutorService executor = Executors.newFixedThreadPool(2);
                try {
                CompletableFuture<StreamResult> stdoutFuture = CompletableFuture.supplyAsync(
                        () -> readStream(process.getInputStream(), properties.getMaxOutputSizeBytes()), executor
                );
                CompletableFuture<StreamResult> stderrFuture = CompletableFuture.supplyAsync(
                        () -> readStream(process.getErrorStream(), properties.getMaxOutputSizeBytes()), executor
                );

                boolean finished = process.waitFor(timeLimitMs, TimeUnit.MILLISECONDS);
                if (!finished) {
                    process.destroyForcibly();
                    long elapsed = elapsedMs(started);
                    return ExecutionResponse.builder()
                            .status(ExecutionStatus.TIME_LIMIT_EXCEEDED)
                            .stdout("")
                            .stderr("Execution timed out")
                            .exitCode(-1)
                            .executionTimeMs(elapsed)
                            .outputTruncated(false)
                            .build();
                }

                stdout = getFuture(stdoutFuture, 1000);
                stderr = getFuture(stderrFuture, 1000);
            } finally {
                executor.shutdownNow();
            }

            int exitCode = process.exitValue();
            long elapsed = elapsedMs(started);
            ExecutionStatus status = switch (exitCode) {
                case 0 -> ExecutionStatus.SUCCESS;
                case 101 -> ExecutionStatus.COMPILE_ERROR;
                default -> ExecutionStatus.RUNTIME_ERROR;
            };

            return ExecutionResponse.builder()
                    .status(status)
                    .stdout(stdout.content())
                    .stderr(stderr.content())
                    .exitCode(exitCode)
                    .executionTimeMs(elapsed)
                    .outputTruncated(stdout.truncated() || stderr.truncated())
                    .build();
        } catch (IOException | InterruptedException | ExecutionException | TimeoutException ex) {
            long elapsed = elapsedMs(started);
            return ExecutionResponse.builder()
                    .status(ExecutionStatus.INTERNAL_ERROR)
                    .stdout("")
                    .stderr("Sandbox failure: " + ex.getMessage())
                    .exitCode(-1)
                    .executionTimeMs(elapsed)
                    .outputTruncated(false)
                    .build();
        } catch (RuntimeException ex) {
            long elapsed = elapsedMs(started);
            return ExecutionResponse.builder()
                    .status(ExecutionStatus.INTERNAL_ERROR)
                    .stdout("")
                    .stderr("Sandbox failure: " + ex.getMessage())
                    .exitCode(-1)
                    .executionTimeMs(elapsed)
                    .outputTruncated(false)
                    .build();
        } finally {
            if (workDir != null) {
                deleteDirectory(workDir);
            }
        }
    }

    private SandboxSpec buildSpec(ExecutionLanguage language) {
        String image = getImage(language);
        return switch (language) {
            case JAVA -> new SandboxSpec(image, "Main.java",
                    "javac Main.java 2> compile.err || { cat compile.err >&2; exit 101; }; java Main");
            case PYTHON -> new SandboxSpec(image, "main.py", "python3 main.py");
            case CPP -> new SandboxSpec(image, "main.cpp",
                    "g++ -O2 -std=c++17 -o main main.cpp 2> compile.err || { cat compile.err >&2; exit 101; }; ./main");
            case JAVASCRIPT -> new SandboxSpec(image, "main.js", "node main.js");
        };
    }

    private String getImage(ExecutionLanguage language) {
        String image = properties.getLanguages().get(language.getKey());
        if (image == null || image.isBlank()) {
            throw new IllegalArgumentException("Docker image is not configured for language: " + language.getKey());
        }
        return image;
    }

    private List<String> buildDockerCommand(SandboxSpec spec, Path workDir, int memoryLimitMb) {
        List<String> command = new ArrayList<>();
        command.add("docker");
        command.add("run");
        command.add("--rm");
        command.add("--network");
        command.add("none");
        command.add("--cpus");
        command.add("1.0");
        command.add("--memory");
        command.add(memoryLimitMb + "m");
        command.add("--memory-swap");
        command.add(memoryLimitMb + "m");
        command.add("--pids-limit");
        command.add("64");
        command.add("--read-only");
        command.add("--tmpfs");
        command.add("/tmp:rw,noexec,nosuid,size=64m");
        command.add("--security-opt");
        command.add("no-new-privileges");
        command.add("--cap-drop");
        command.add("ALL");
        command.add("--ulimit");
        command.add("nproc=64:64");
        command.add("--ulimit");
        command.add("fsize=1048576");
        command.add("--user");
        command.add("65534:65534");
        command.add("-v");
        command.add(workDir.toAbsolutePath() + ":/workspace");
        command.add("-w");
        command.add("/workspace");
        command.add(spec.image());
        command.add("sh");
        command.add("-lc");
        command.add(spec.command());
        return command;
    }

    private StreamResult readStream(InputStream inputStream, int maxBytes) {
        try {
            byte[] buffer = new byte[4096];
            int total = 0;
            boolean truncated = false;
            StringBuilder out = new StringBuilder();
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                int allowed = Math.min(read, maxBytes - total);
                if (allowed > 0) {
                    out.append(new String(buffer, 0, allowed, StandardCharsets.UTF_8));
                    total += allowed;
                }
                if (total >= maxBytes) {
                    truncated = true;
                    break;
                }
            }
            return new StreamResult(out.toString(), truncated);
        } catch (IOException e) {
            return new StreamResult("", true);
        }
    }

    private StreamResult getFuture(CompletableFuture<StreamResult> future, long timeoutMs)
            throws InterruptedException, ExecutionException, TimeoutException {
        return future.get(timeoutMs, TimeUnit.MILLISECONDS);
    }

    private long elapsedMs(long startedNano) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedNano);
    }

    private void deleteDirectory(Path root) {
        try (var paths = Files.walk(root)) {
            paths.sorted((a, b) -> b.getNameCount() - a.getNameCount())
                    .forEach(path -> {
                        try {
                            Files.deleteIfExists(path);
                        } catch (IOException ignored) {
                        }
                    });
        } catch (IOException ignored) {
        }
    }

    private record SandboxSpec(String image, String sourceFileName, String command) {
    }

    private record StreamResult(String content, boolean truncated) {
    }
}
