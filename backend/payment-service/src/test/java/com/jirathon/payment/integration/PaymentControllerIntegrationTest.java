package com.jirathon.payment.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jirathon.payment.dto.request.CreatePaymentRequest;
import com.jirathon.payment.dto.response.PaymentInitiationResponse;
import com.jirathon.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class PaymentControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PaymentService paymentService;

    @Test
    void initiatePayment_shouldReturn200AndResponseBody() throws Exception {
        PaymentInitiationResponse response = new PaymentInitiationResponse();
        response.setTransactionId("txn-123");
        response.setPayuTxnId("payu-123");
        response.setOriginalAmount(1000.0);
        response.setDiscountAmount(100.0);
        response.setPayableAmount(900.0);
        response.setPaymentUrl("https://payu.example/pay");
        response.setPayuFormData(Map.of("key", "value"));

        when(paymentService.initiatePayment(any(CreatePaymentRequest.class))).thenReturn(response);

        CreatePaymentRequest request = new CreatePaymentRequest();
        request.setRegistrationId("reg-1");
        request.setContestId("contest-1");
        request.setUserId("user-1");
        request.setName("Jane Doe");
        request.setEmail("jane@example.com");
        request.setPhone("9999999999");
        request.setAmount(1000.0);

        mockMvc.perform(post("/api/v1/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Payment initiated"))
                .andExpect(jsonPath("$.data.transactionId").value("txn-123"));
    }

    @Test
    void initiatePayment_shouldReturn400ForInvalidPayload() throws Exception {
        CreatePaymentRequest invalidRequest = new CreatePaymentRequest();
        invalidRequest.setContestId("contest-1");
        invalidRequest.setUserId("user-1");
        invalidRequest.setName("Jane Doe");
        invalidRequest.setEmail("invalid-email");
        invalidRequest.setPhone("9999999999");
        invalidRequest.setAmount(0.0);

        mockMvc.perform(post("/api/v1/payments/initiate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}
