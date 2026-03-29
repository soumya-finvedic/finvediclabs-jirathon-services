package com.jirathon.payment.repository;

import com.jirathon.payment.model.Coupon;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

// @Repository // TEMPORARILY DISABLED
public interface CouponRepository extends MongoRepository<Coupon, String> {

    Optional<Coupon> findByCodeIgnoreCase(String code);
}
