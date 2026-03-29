package com.jirathon.payment.controller;

import com.jirathon.payment.dto.ApiResponse;
import com.jirathon.payment.dto.request.CreatePaymentRequest;
import com.jirathon.payment.dto.request.RefundRequest;
import com.jirathon.payment.dto.response.PaymentInitiationResponse;
import com.jirathon.payment.dto.response.PaymentResponse;
import com.jirathon.payment.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/initiate")
    public ResponseEntity<ApiResponse<PaymentInitiationResponse>> initiate(
            @Valid @RequestBody CreatePaymentRequest request
    ) {
        PaymentInitiationResponse response = paymentService.initiatePayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment initiated", response));
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getPayment(@PathVariable String transactionId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getByTransactionId(transactionId)));
    }

    @GetMapping("/registration/{registrationId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> getByRegistration(@PathVariable String registrationId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.getByRegistrationId(registrationId)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<PaymentResponse>>> listByUser(@RequestParam String userId) {
        return ResponseEntity.ok(ApiResponse.success(paymentService.listByUser(userId)));
    }

    @PostMapping("/{transactionId}/refund")
    public ResponseEntity<ApiResponse<PaymentResponse>> refund(
            @PathVariable String transactionId,
            @Valid @RequestBody RefundRequest request
    ) {
        PaymentResponse response = paymentService.refund(transactionId, request);
        return ResponseEntity.ok(ApiResponse.success("Refund processed", response));
    }

    @PostMapping("/{transactionId}/simulate-success")
    public ResponseEntity<ApiResponse<PaymentResponse>> simulateSuccess(@PathVariable String transactionId) {
        PaymentResponse response = paymentService.simulateSuccess(transactionId);
        return ResponseEntity.ok(ApiResponse.success("Payment marked successful", response));
    }
}
