package com.jirathon.notification.service;

import com.jirathon.notification.model.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }
    
    @Value("${app.email.from:noreply@jirathon.com}")
    private String fromEmail;
    
    @Value("${app.email.from-name:Jirathon}")
    private String fromName;
    
    /**
     * Send notification email
     */
    public void sendNotificationEmail(Notification notification) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        try {
            helper.setFrom(fromEmail, fromName);
        } catch (UnsupportedEncodingException e) {
            throw new MessagingException("Invalid sender name encoding", e);
        }
        helper.setTo(notification.getEmail());
        helper.setSubject(notification.getSubject() != null ? notification.getSubject() : notification.getTitle());
        
        String htmlContent = buildHtmlContent(notification);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
        log.info("Email sent to {} for notification {}", notification.getEmail(), notification.getId());
    }
    
    /**
     * Send simple text email
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        
        mailSender.send(message);
        log.info("Simple email sent to {}", to);
    }
    
    private String buildHtmlContent(Notification notification) {
        String title = notification.getTitle();
        String message = notification.getMessage();
        String fullName = notification.getFullName();
        String type = notification.getType().toString();
        
        return String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body { font-family: Arial, sans-serif; color: #333; line-height: 1.6; }
                        .container { max-width: 600px; margin: 0 auto; padding: 20px; background-color: #f9f9f9; }
                        .header { background-color: #4CAF50; color: white; padding: 20px; text-align: center; border-radius: 5px 5px 0 0; }
                        .content { background-color: white; padding: 20px; border-radius: 0 0 5px 5px; }
                        .footer { margin-top: 20px; font-size: 12px; color: #666; text-align: center; }
                        .notification-type { display: inline-block; background-color: #4CAF50; color: white; padding: 5px 10px; border-radius: 3px; font-size: 12px; margin-bottom: 10px; }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>%s</h1>
                        </div>
                        <div class="content">
                            <p>Hi %s,</p>
                            <div class="notification-type">%s</div>
                            <h2>%s</h2>
                            <p>%s</p>
                            <p style="margin-top: 30px; color: #999; font-size: 12px;">
                                This is an automated notification from Jirathon. Please do not reply to this email.
                            </p>
                        </div>
                        <div class="footer">
                            <p>&copy; 2026 Jirathon. All rights reserved.</p>
                        </div>
                    </div>
                </body>
                </html>
                """, fromName, fullName, type, title, message);
    }
}
