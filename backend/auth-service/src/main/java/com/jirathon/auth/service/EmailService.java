package com.jirathon.auth.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String fromName;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.from-address:noreply@jirathon.io}") String fromAddress,
            @Value("${spring.mail.from-name:Jirathon}") String fromName
    ) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
    }

    public void sendOtpEmail(String to, String otp, String purpose) {
        String subject = switch (purpose) {
            case "EMAIL_VERIFICATION" -> "Verify your Jirathon account";
            case "PASSWORD_RESET" -> "Reset your Jirathon password";
            case "LOGIN" -> "Your Jirathon login OTP";
            default -> "Your Jirathon verification code";
        };

        String htmlBody = buildOtpEmailHtml(otp, purpose);
        sendHtmlEmail(to, subject, htmlBody);
    }

    public void sendPasswordResetEmail(String to, String resetLink) {
        String subject = "Reset your Jirathon password";
        String htmlBody = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #1A73E8;">Reset Your Password</h2>
                <p>You requested a password reset for your Jirathon account.</p>
                <p>Click the link below to set a new password. This link expires in 1 hour.</p>
                <a href="%s" style="display: inline-block; padding: 12px 24px;
                   background-color: #1A73E8; color: white; text-decoration: none;
                   border-radius: 6px; margin: 16px 0;">Reset Password</a>
                <p style="color: #666; font-size: 13px;">
                    If you didn't request this, ignore this email. Your password won't change.
                </p>
            </div>
            """.formatted(resetLink);

        sendHtmlEmail(to, subject, htmlBody);
    }

    public void sendVerificationConfirmationEmail(String to, String displayName) {
        String subject = "Email verified successfully";
        String htmlBody = buildVerificationConfirmationHtml(displayName);
        sendHtmlEmail(to, subject, htmlBody);
    }

    public void sendPasswordChangeConfirmationEmail(String to, String displayName) {
        String subject = "Your password has been changed";
        String htmlBody = buildPasswordChangeConfirmationHtml(displayName);
        sendHtmlEmail(to, subject, htmlBody);
    }

    private void sendHtmlEmail(String to, String subject, String htmlBody) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(fromAddress, fromName);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlBody, true);
            mailSender.send(message);
            log.info("Email sent to {}: {}", to, subject);
        } catch (MessagingException | java.io.UnsupportedEncodingException | RuntimeException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            throw new IllegalStateException("Email delivery failed", e);
        }
    }

    private String buildOtpEmailHtml(String otp, String purpose) {
        String action = switch (purpose) {
            case "EMAIL_VERIFICATION" -> "verify your email address";
            case "PASSWORD_RESET" -> "reset your password";
            case "LOGIN" -> "log in to your account";
            default -> "complete your request";
        };

        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #1A73E8;">Jirathon Verification Code</h2>
                <p>Use the code below to %s:</p>
                <div style="background: #f4f4f4; padding: 20px; text-align: center;
                     border-radius: 8px; margin: 16px 0;">
                    <span style="font-size: 32px; font-weight: bold; letter-spacing: 8px;
                          color: #333;">%s</span>
                </div>
                <p style="color: #666; font-size: 13px;">
                    This code expires in 10 minutes. Do not share it with anyone.
                </p>
            </div>
            """.formatted(action, otp);
    }

    private String buildVerificationConfirmationHtml(String displayName) {
        String greeting = displayName != null && !displayName.isBlank() 
            ? "Hi " + displayName + "!" 
            : "Thank you";
        
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #1A73E8;">✅ Email Verified Successfully</h2>
                <p>%s</p>
                <p>Your email address has been verified and your Jirathon account is now fully activated.</p>
                <div style="background: #E8F5E9; border-left: 4px solid #4CAF50; padding: 16px; 
                            margin: 20px 0; border-radius: 4px;">
                    <p style="color: #2E7D32; margin: 0;">
                        ✓ Email verification complete<br/>
                        ✓ Account is now active<br/>
                        ✓ You can start using all features
                    </p>
                </div>
                <p style="margin-top: 20px;">You can now log in with your email and password to access your Jirathon account.</p>
                <p style="color: #666; font-size: 13px; margin-top: 30px;">
                    If you did not perform this action, please contact our support team immediately.
                </p>
            </div>
            """.formatted(greeting);
    }

    private String buildPasswordChangeConfirmationHtml(String displayName) {
        String greeting = displayName != null && !displayName.isBlank() 
            ? "Hi " + displayName + "!" 
            : "Hello";
        
        return """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;">
                <h2 style="color: #1A73E8;">🔒 Your Password Has Been Changed</h2>
                <p>%s</p>
                <p>Your Jirathon account password was successfully changed.</p>
                <div style="background: #E3F2FD; border-left: 4px solid #1976D2; padding: 16px; 
                            margin: 20px 0; border-radius: 4px;">
                    <p style="color: #0D47A1; margin: 0;">
                        ✓ Password changed successfully<br/>
                        ✓ You will need to log in again with your new password<br/>
                        ✓ All previous sessions have been signed out for security
                    </p>
                </div>
                <p style="margin-top: 20px; color: #666;">
                    <strong>Didn't make this change?</strong><br/>
                    If you didn't request a password change, your account may have been compromised. 
                    Please contact our support team immediately.
                </p>
                <p style="color: #666; font-size: 13px; margin-top: 30px;">
                    For security reasons, you will need to log in again with your new password on all devices.
                </p>
            </div>
            """.formatted(greeting);
    }
}