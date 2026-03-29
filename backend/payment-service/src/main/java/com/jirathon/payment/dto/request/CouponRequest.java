package com.jirathon.payment.dto.request;

import com.jirathon.payment.model.enums.CouponType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;

public class CouponRequest {

    @NotBlank(message = "code is required")
    private String code;

    @NotNull(message = "type is required")
    private CouponType type;

    @DecimalMin(value = "0.01", message = "value must be greater than 0")
    private double value;

    @DecimalMin(value = "0.0", message = "maxDiscount must be >= 0")
    private double maxDiscount;

    @DecimalMin(value = "0.0", message = "minAmount must be >= 0")
    private double minAmount;

    @Min(value = 1, message = "usageLimit must be at least 1")
    private int usageLimit;

    private Instant validFrom;
    private Instant validUntil;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public CouponType getType() {
        return type;
    }

    public void setType(CouponType type) {
        this.type = type;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public double getMaxDiscount() {
        return maxDiscount;
    }

    public void setMaxDiscount(double maxDiscount) {
        this.maxDiscount = maxDiscount;
    }

    public double getMinAmount() {
        return minAmount;
    }

    public void setMinAmount(double minAmount) {
        this.minAmount = minAmount;
    }

    public int getUsageLimit() {
        return usageLimit;
    }

    public void setUsageLimit(int usageLimit) {
        this.usageLimit = usageLimit;
    }

    public Instant getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(Instant validFrom) {
        this.validFrom = validFrom;
    }

    public Instant getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(Instant validUntil) {
        this.validUntil = validUntil;
    }
}
