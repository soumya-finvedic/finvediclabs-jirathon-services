package com.jirathon.payment.repository;

import com.jirathon.payment.model.PaymentTransaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentTransactionRepository extends MongoRepository<PaymentTransaction, String> {

    Optional<PaymentTransaction> findByRegistrationId(String registrationId);

    Optional<PaymentTransaction> findByPayuTxnId(String payuTxnId);

    List<PaymentTransaction> findByUserIdOrderByCreatedAtDesc(String userId);
}
