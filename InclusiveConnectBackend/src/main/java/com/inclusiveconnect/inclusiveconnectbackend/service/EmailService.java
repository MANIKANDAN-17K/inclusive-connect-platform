package com.inclusiveconnect.inclusiveconnectbackend.service;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String firstName, String token);
    void sendPasswordResetEmail(String toEmail, String firstName, String token);
}