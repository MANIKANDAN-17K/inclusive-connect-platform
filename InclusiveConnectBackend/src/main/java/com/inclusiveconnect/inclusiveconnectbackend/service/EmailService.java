package com.inclusiveconnect.inclusiveconnectbackend.service;

import java.util.Map;

public interface EmailService {
    void sendVerificationEmail(String toEmail, String firstName, String token);

    void sendPasswordResetEmail(String toEmail, String firstName, String token);

    void sendHtmlNotificationEmail(String toEmail, String userName, String title, String actionUrl, String actionText,
            String templateName, Map<String, String> placeholders);
}