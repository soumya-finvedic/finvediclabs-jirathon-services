package com.jirathon.payment.dto.response;

import com.jirathon.payment.model.enums.PaymentStatus;

import java.time.Instant;

public class PaymentResponse {

    private String id;
    private String registrationId;
    private String contestId;
    private String userId;
    private double originalAmount;
    private double discountAmount;
    private double payableAmount;
    private String couponCode;
    private PaymentStatus status;
    private String payuTxnId;
    private String payuPaymentId;
    private String payuStatus;
    private String failureReason;
    private Instant createdAt;
    private Instant updatedAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }

    public String getContestId() {
        return contestId;
    }

    public void setContestId(String contestId) {
        this.contestId = contestId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getOriginalAmount() {
        return originalAmount;
    }

    public void setOriginalAmount(double originalAmount) {
        this.originalAmount = originalAmount;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

    public double getPayableAmount() {
        return payableAmount;
    }

    public void setPayableAmount(double payableAmount) {
        this.payableAmount = payableAmount;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public String getPayuTxnId() {
        return payuTxnId;
    }

    public void setPayuTxnId(String payuTxnId) {
        this.payuTxnId = payuTxnId;
    }

    public String getPayuPaymentId() {
        return payuPaymentId;
    }

    public void setPayuPaymentId(String payuPaymentId) {
        this.payuPaymentId = payuPaymentId;
    }

    public String getPayuStatus() {
        return payuStatus;
    }

    public void setPayuStatus(String payuStatus) {
        this.payuStatus = payuStatus;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
