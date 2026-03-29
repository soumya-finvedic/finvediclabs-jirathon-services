package com.jirathon.payment.controller;

import com.jirathon.payment.dto.ApiResponse;
import com.jirathon.payment.dto.request.CouponRequest;
import com.jirathon.payment.dto.response.CouponResponse;
import com.jirathon.payment.dto.response.CouponValidationResponse;
import com.jirathon.payment.service.CouponService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

// COUPON FUNCTIONALITY TEMPORARILY DISABLED
// To re-enable: uncomment @RestController and @RequestMapping below
// @RestController
// @RequestMapping("/api/v1/coupons")
public class CouponController {

    private final CouponService couponService;

    public CouponController(CouponService couponService) {
        this.couponService = couponService;
    }

    // @PostMapping("/admin")
    public ResponseEntity<ApiResponse<CouponResponse>> create(@Valid @RequestBody CouponRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Coupon created", couponService.create(request)));
    }

    // @PutMapping("/admin/{code}")
    public ResponseEntity<ApiResponse<CouponResponse>> update(
            @PathVariable String code,
            @Valid @RequestBody CouponRequest request
    ) {
        return ResponseEntity.ok(ApiResponse.success("Coupon updated", couponService.update(code, request)));
    }

    // @PutMapping("/admin/{code}/deactivate")
    public ResponseEntity<ApiResponse<CouponResponse>> deactivate(@PathVariable String code) {
        return ResponseEntity.ok(ApiResponse.success("Coupon deactivated", couponService.deactivate(code)));
    }

    // @GetMapping("/{code}/validate")
    public ResponseEntity<ApiResponse<CouponValidationResponse>> validate(
            @PathVariable String code,
            @RequestParam double amount
    ) {
        return ResponseEntity.ok(ApiResponse.success(couponService.validate(code, amount)));
    }
}
