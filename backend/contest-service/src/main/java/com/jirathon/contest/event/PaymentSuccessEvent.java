package com.jirathon.contest.event;

public class PaymentSuccessEvent {

    private String paymentTransactionId;
    private String registrationId;
    private String contestId;
    private String userId;
    private double payableAmount;
    private String payuTxnId;
    private String payuPaymentId;

    public PaymentSuccessEvent() {}

    public String getPaymentTransactionId() { return paymentTransactionId; }
    public void setPaymentTransactionId(String paymentTransactionId) { this.paymentTransactionId = paymentTransactionId; }

    public String getRegistrationId() { return registrationId; }
    public void setRegistrationId(String registrationId) { this.registrationId = registrationId; }

    public String getContestId() { return contestId; }
    public void setContestId(String contestId) { this.contestId = contestId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public double getPayableAmount() { return payableAmount; }
    public void setPayableAmount(double payableAmount) { this.payableAmount = payableAmount; }

    public String getPayuTxnId() { return payuTxnId; }
    public void setPayuTxnId(String payuTxnId) { this.payuTxnId = payuTxnId; }

    public String getPayuPaymentId() { return payuPaymentId; }
    public void setPayuPaymentId(String payuPaymentId) { this.payuPaymentId = payuPaymentId; }

    @Override
    public String toString() {
        return "PaymentSuccessEvent{paymentTransactionId='" + paymentTransactionId +
               "', registrationId='" + registrationId + "', contestId='" + contestId +
               "', userId='" + userId + "', payableAmount=" + payableAmount + "}";
    }
}
