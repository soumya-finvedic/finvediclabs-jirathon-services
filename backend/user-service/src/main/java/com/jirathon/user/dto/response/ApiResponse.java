package com.jirathon.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private String error;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data, String error) {
        this.success = success; this.message = message; this.data = data; this.error = error;
    }

    public static <T> Builder<T> builder() { return new Builder<>(); }

    public static class Builder<T> {
        private boolean success;
        private String message, error;
        private T data;
        public Builder<T> success(boolean v) { this.success = v; return this; }
        public Builder<T> message(String v)  { this.message = v; return this; }
        public Builder<T> data(T v)          { this.data = v; return this; }
        public Builder<T> error(String v)    { this.error = v; return this; }
        public ApiResponse<T> build()        { return new ApiResponse<>(success, message, data, error); }
    }

    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder().success(true).data(data).build();
    }

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder().success(true).message(message).data(data).build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder().success(true).message(message).build();
    }

    public static <T> ApiResponse<T> error(String error) {
        return ApiResponse.<T>builder().success(false).error(error).build();
    }

    public boolean isSuccess()  { return success; }
    public String getMessage()  { return message; }
    public T getData()          { return data; }
    public String getError()    { return error; }
    public void setSuccess(boolean success) { this.success = success; }
    public void setMessage(String message)  { this.message = message; }
    public void setData(T data)             { this.data = data; }
    public void setError(String error)      { this.error = error; }
}
