package com.jirathon.contest.dto.request;

import jakarta.validation.constraints.NotBlank;

public class ConfirmRegistrationPaymentRequest {

    @NotBlank(message = "paymentTransactionId is required")
    private String paymentTransactionId;

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }
}