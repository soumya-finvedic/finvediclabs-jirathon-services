package com.jirathon.payment.service;

import com.jirathon.payment.dto.request.CouponRequest;
import com.jirathon.payment.dto.response.CouponResponse;
import com.jirathon.payment.dto.response.CouponValidationResponse;
import com.jirathon.payment.model.Coupon;
import org.springframework.stereotype.Service;

@Service
public class CouponService {

    public CouponResponse create(CouponRequest request) {
        throw new IllegalArgumentException("Coupon functionality is temporarily disabled.");
    }

    public CouponResponse update(String code, CouponRequest request) {
        throw new IllegalArgumentException("Coupon functionality is temporarily disabled.");
    }

    public CouponResponse deactivate(String code) {
        throw new IllegalArgumentException("Coupon functionality is temporarily disabled.");
    }

    public CouponValidationResponse validate(String code, double amount) {
        throw new IllegalArgumentException("Coupon functionality is temporarily disabled.");
    }

    public CouponValidationResponse validateCoupon(Coupon coupon, double amount) {
        CouponValidationResponse response = new CouponValidationResponse();
        response.setCode(coupon != null && coupon.getCode() != null ? coupon.getCode() : "UNKNOWN");
        response.setValid(false);
        response.setDiscountAmount(0.0);
        response.setFinalAmount(amount);
        response.setMessage("Coupon functionality is temporarily disabled.");
        return response;
    }

    public Coupon findByCode(String code) {
        throw new IllegalArgumentException("Coupon functionality is temporarily disabled.");
    }

    public void incrementUsage(Coupon coupon) {
        // No-op
    }
}