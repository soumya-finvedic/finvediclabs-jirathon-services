package com.jirathon.payment.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class RefundRequest {

    @NotNull(message = "amount is required")
    @DecimalMin(value = "1.0", message = "refund amount must be at least 1")
    private Double amount;

    @NotBlank(message = "reason is required")
    private String reason;

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
