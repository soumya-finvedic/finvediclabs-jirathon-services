package com.jirathon.payment.dto.response;

import java.util.Map;

public class PaymentInitiationResponse {

    private String transactionId;
    private String payuTxnId;
    private double originalAmount;
    private double discountAmount;
    private double payableAmount;
    private String paymentUrl;
    private Map<String, String> payuFormData;

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getPayuTxnId() {
        return payuTxnId;
    }

    public void setPayuTxnId(String payuTxnId) {
        this.payuTxnId = payuTxnId;
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

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.paymentUrl = paymentUrl;
    }

    public Map<String, String> getPayuFormData() {
        return payuFormData;
    }

    public void setPayuFormData(Map<String, String> payuFormData) {
        this.payuFormData = payuFormData;
    }
}
