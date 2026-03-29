package com.jirathon.execution.dto.response;

import com.jirathon.execution.model.ExecutionStatus;
public class ExecutionResponse {

    private ExecutionStatus status;
    private String stdout;
    private String stderr;
    private int exitCode;
    private long executionTimeMs;
    private boolean outputTruncated;

    public ExecutionResponse() {
    }

    public ExecutionResponse(ExecutionStatus status, String stdout, String stderr, int exitCode,
                             long executionTimeMs, boolean outputTruncated) {
        this.status = status;
        this.stdout = stdout;
        this.stderr = stderr;
        this.exitCode = exitCode;
        this.executionTimeMs = executionTimeMs;
        this.outputTruncated = outputTruncated;
    }

    public static Builder builder() {
        return new Builder();
    }

    public ExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ExecutionStatus status) {
        this.status = status;
    }

    public String getStdout() {
        return stdout;
    }

    public void setStdout(String stdout) {
        this.stdout = stdout;
    }

    public String getStderr() {
        return stderr;
    }

    public void setStderr(String stderr) {
        this.stderr = stderr;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    public boolean isOutputTruncated() {
        return outputTruncated;
    }

    public void setOutputTruncated(boolean outputTruncated) {
        this.outputTruncated = outputTruncated;
    }

    public static final class Builder {
        private ExecutionStatus status;
        private String stdout;
        private String stderr;
        private int exitCode;
        private long executionTimeMs;
        private boolean outputTruncated;

        private Builder() {
        }

        public Builder status(ExecutionStatus status) {
            this.status = status;
            return this;
        }

        public Builder stdout(String stdout) {
            this.stdout = stdout;
            return this;
        }

        public Builder stderr(String stderr) {
            this.stderr = stderr;
            return this;
        }

        public Builder exitCode(int exitCode) {
            this.exitCode = exitCode;
            return this;
        }

        public Builder executionTimeMs(long executionTimeMs) {
            this.executionTimeMs = executionTimeMs;
            return this;
        }

        public Builder outputTruncated(boolean outputTruncated) {
            this.outputTruncated = outputTruncated;
            return this;
        }

        public ExecutionResponse build() {
            return new ExecutionResponse(status, stdout, stderr, exitCode, executionTimeMs, outputTruncated);
        }
    }
}
