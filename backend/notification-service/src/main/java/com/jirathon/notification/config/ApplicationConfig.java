package com.jirathon.notification.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.beans.factory.annotation.Value;
import java.util.Properties;

@Configuration
@EnableMongoRepositories(basePackages = "com.jirathon.notification.repository")
public class ApplicationConfig {
    
    @Value("${app.mail.host:smtp.mailtrap.io}")
    private String mailHost;
    
    @Value("${app.mail.port:465}")
    private int mailPort;
    
    @Value("${app.mail.username:}")
    private String mailUsername;
    
    @Value("${app.mail.password:}")
    private String mailPassword;
    
    /**
     * Configure JavaMailSender for email notifications
     */
    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);
        
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");
        props.put("mail.debug", "false");
        
        return mailSender;
    }
}
