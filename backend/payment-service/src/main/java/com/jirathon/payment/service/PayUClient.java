package com.jirathon.payment.service;

import com.jirathon.payment.config.PayUProperties;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Component
public class PayUClient {

    private final PayUProperties properties;

    public PayUClient(PayUProperties properties) {
        this.properties = properties;
    }

    public Map<String, String> buildPaymentFormData(
            String txnId,
            String amount,
            String name,
            String email,
            String phone,
            String productInfo,
            String udf1,
            String udf2
    ) {
        String hashString = safe(properties.getKey()) + "|" +
                safe(txnId) + "|" +
                safe(amount) + "|" +
                safe(productInfo) + "|" +
                safe(name) + "|" +
                safe(email) + "|" +
                safe(udf1) + "|" +
                safe(udf2) + "|" +
                safe("") + "|" +    // udf3
                safe("") + "|" +    // udf4
                safe("") + "|" +    // udf5
                safe("") + "|" +    // extra1
                safe("") + "|" +    // extra2
                safe("") + "|" +    // extra3
                safe("") + "|" +    // extra4
                safe("") + "|" +    // extra5
                safe(properties.getSalt());

        Map<String, String> payload = new HashMap<>();
        payload.put("key", properties.getKey());
        payload.put("txnid", txnId);
        payload.put("amount", amount);
        payload.put("productinfo", productInfo);
        payload.put("firstname", name);
        payload.put("email", email);
        payload.put("phone", phone);
        payload.put("surl", properties.getSuccessUrl());
        payload.put("furl", properties.getFailureUrl());
        payload.put("udf1", udf1);
        payload.put("udf2", udf2);
        System.err.println("HASH INPUT: " + hashString);
        payload.put("hash", sha512(hashString));


        System.err.println("HASH OUTPUT LENGTH: " + sha512(hashString).length());
        System.err.println("HASH OUTPUT: " + sha512(hashString));
        System.err.println("PAYLOAD: " + payload);
        return payload;
    }

    public boolean validateWebhookSignature(String receivedSecret) {
        System.err.println(("Received webhook secret: " + receivedSecret));
        System.err.println("Config:   " + properties.getWebhookSecret());
        return safe(properties.getWebhookSecret()).equals(safe(receivedSecret));
    }

    public String getPaymentUrl() {
        return properties.getBaseUrl() + "/_payment";
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static String sha512(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b & 0xff));  // ← add & 0xff
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-512 algorithm is not available", e);
        }
    }
}
