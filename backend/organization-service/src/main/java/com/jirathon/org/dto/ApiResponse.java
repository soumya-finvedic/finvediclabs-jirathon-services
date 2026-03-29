package com.jirathon.org.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data) {
        this.success = success; this.message = message; this.data = data;
    }

    public static <T> Builder<T> builder() { return new Builder<>(); }

    public static class Builder<T> {
        private boolean success;
        private String message;
        private T data;
        public Builder<T> success(boolean v) { this.success = v; return this; }
        public Builder<T> message(String v)  { this.message = v; return this; }
        public Builder<T> data(T v)          { this.data = v; return this; }
        public ApiResponse<T> build()        { return new ApiResponse<>(success, message, data); }
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder().success(true).data(data).build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder().success(true).message(message).data(data).build();
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder().success(false).message(message).build();
    }

    public boolean isSuccess()  { return success; }
    public String getMessage()  { return message; }
    public T getData()          { return data; }
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message)  { this.message = message; }
    public void setData(T data)             { this.data = data; }
}
