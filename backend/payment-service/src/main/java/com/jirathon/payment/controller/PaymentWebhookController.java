package com.jirathon.payment.controller;

import com.jirathon.payment.dto.ApiResponse;
import com.jirathon.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class PaymentWebhookController {

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    private final PaymentService paymentService;

    public PaymentWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/api/v1/payments/webhooks/payu")
    public ResponseEntity<ApiResponse<String>> payuWebhook(
            @RequestHeader(name = "X-Payu-Secret", required = false) String webhookSecret,
            @RequestParam Map<String, String> payload
    ) {
        paymentService.processWebhook(payload, webhookSecret);
        return ResponseEntity.ok(ApiResponse.success("Webhook processed", "OK"));
    }

    @PostMapping("/payment/success")
    public ResponseEntity<Void> payuSuccess(@RequestParam Map<String, String> payload) {
        try {
            paymentService.processWebhook(payload, null);
        } catch (Exception ignored) {}

        String txnid  = payload.getOrDefault("txnid", "");
        String status = payload.getOrDefault("status", "failure");
        String udf1   = payload.getOrDefault("udf1", "");
        String udf2   = payload.getOrDefault("udf2", "");

        String frontendUrl = frontendBaseUrl + "/payment/callback"
                + "?status=" + status
                + "&txnid="  + txnid
                + "&udf1="   + udf1
                + "&udf2="   + udf2;

        System.err.println(">>> frontendBaseUrl value: " + frontendBaseUrl);  // ADD THIS
        System.err.println(">>> Redirecting to: " + frontendUrl);

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", frontendUrl)
                .build();
    }

    @PostMapping("/payment/failure")
    public ResponseEntity<Void> payuFailure(@RequestParam Map<String, String> payload) {
        String udf2 = payload.getOrDefault("udf2", "");

        String frontendUrl = frontendBaseUrl + "/payment/callback"
                + "?status=failure"
                + "&udf2=" + udf2;

        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", frontendUrl)
                .build();
    }
}