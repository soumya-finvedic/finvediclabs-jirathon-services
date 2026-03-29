package com.jirathon.contest.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UpdateRegistrationDetailsRequest {

    @NotBlank(message = "Team name is required")
    @Size(max = 120, message = "Team name must be at most 120 characters")
    private String teamName;

    @NotBlank(message = "Contact number is required")
    @Size(max = 30, message = "Contact number must be at most 30 characters")
    private String contactNumber;

    @Size(max = 40, message = "Coupon code must be at most 40 characters")
    private String couponCode;

    public String getTeamName() { return teamName; }
    public String getContactNumber() { return contactNumber; }
    public String getCouponCode() { return couponCode; }

    public void setTeamName(String teamName) { this.teamName = teamName; }
    public void setContactNumber(String contactNumber) { this.contactNumber = contactNumber; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }
}
