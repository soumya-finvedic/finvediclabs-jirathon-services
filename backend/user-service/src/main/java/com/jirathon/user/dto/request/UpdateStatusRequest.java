package com.jirathon.user.dto.request;

import com.jirathon.user.model.enums.UserStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateStatusRequest {

    @NotBlank(message = "User ID is required")
    private String userId;

    @NotNull(message = "Status is required")
    private UserStatus status;

    private String reason;

    public String getUserId()       { return userId; }
    public UserStatus getStatus()   { return status; }
    public String getReason()       { return reason; }
    public void setUserId(String userId)      { this.userId = userId; }
    public void setStatus(UserStatus status)  { this.status = status; }
    public void setReason(String reason)      { this.reason = reason; }
}
