package com.inclusiveconnect.inclusiveconnectbackend.service.impl;

import com.inclusiveconnect.inclusiveconnectbackend.service.EmailService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String toEmail, String firstName, String token) {
        String link = frontendUrl + "/auth/verify-email?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Verify your Inclusive Connect account");
        message.setText(
                "Hi " + firstName + ",\n\n" +
                        "Welcome to Inclusive Connect! Please verify your email by clicking the link below:\n\n" +
                        link + "\n\n" +
                        "This link expires in 24 hours.\n\n" +
                        "If you didn't create this account, you can ignore this email."
        );

        try {
            mailSender.send(message);
        } catch (Exception e) {
            System.err.println("Failed to send verification email to " + toEmail + ": " + e.getMessage());
        }
    }
    @Override
    public void sendPasswordResetEmail(String toEmail, String firstName, String token) {
        String link = frontendUrl + "/auth/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset your Inclusive Connect password");
        message.setText(
                "Hi " + firstName + ",\n\n" +
                        "We received a request to reset your password. Click the link below to choose a new one:\n\n" +
                        link + "\n\n" +
                        "This link expires in 1 hour. If you didn't request this, you can ignore this email."
        );

        try {
            mailSender.send(message);
        } catch (Exception e) {
            // TODO: log this once SLF4J logging calls are added to the service layer.
            // Swallowed for local dev without SMTP configured — see sendVerificationEmail.
        }
    }

}