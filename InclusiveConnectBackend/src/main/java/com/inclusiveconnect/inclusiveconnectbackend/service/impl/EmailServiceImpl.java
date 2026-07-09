package com.inclusiveconnect.inclusiveconnectbackend.service.impl;

import com.inclusiveconnect.inclusiveconnectbackend.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import jakarta.mail.internet.MimeMessage;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
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
                        "If you didn't create this account, you can ignore this email.");

        try {
            mailSender.send(message);
            log.info("Email Sent to: {}, Type: Verification, Timestamp: {}", toEmail, java.time.LocalDateTime.now());
        } catch (Exception e) {
            log.error("Email Failed to: {}, Type: Verification, Error: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendPasswordResetEmail(String toEmail, String firstName, String token) {
        String link = frontendUrl + "/auth/reset-password?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Reset your Inclusive Connect password");
        message.setText(
                "Hi " + firstName + ",\n\n" +
                        "We received a request to reset your password. Click the link below to choose a new one:\n\n" +
                        link + "\n\n" +
                        "This link expires in 1 hour. If you didn't request this, you can ignore this email.");

        try {
            mailSender.send(message);
            log.info("Email Sent to: {}, Type: PasswordReset, Timestamp: {}", toEmail, java.time.LocalDateTime.now());
        } catch (Exception e) {
            log.error("Email Failed to: {}, Type: PasswordReset, Error: {}", toEmail, e.getMessage());
        }
    }

    @Override
    @Async
    public void sendHtmlNotificationEmail(String toEmail, String userName, String title, String actionUrl,
            String actionText, String templateName, Map<String, String> placeholders) {
        try {
            // 1. Load layout
            String layoutHtml = loadTemplate("layout.html");
            if (layoutHtml == null) {
                log.error("Failed to load email layout template");
                return;
            }

            // 2. Load specific body template
            String bodyContent = loadTemplate(templateName);
            if (bodyContent == null) {
                log.error("Failed to load email body template: {}", templateName);
                return;
            }

            // 3. Process placeholders inside body template first
            if (placeholders != null) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue() != null ? entry.getValue() : "";
                    bodyContent = bodyContent.replace("{{" + key + "}}", value);
                }
            }

            // 4. Inject body content and other dynamic values into the layout
            String finalHtml = layoutHtml
                    .replace("{{title}}", title != null ? title : "")
                    .replace("{{userName}}", userName != null ? userName : "")
                    .replace("{{bodyContent}}", bodyContent)
                    .replace("{{actionUrl}}", actionUrl != null ? actionUrl : "")
                    .replace("{{actionText}}", actionText != null ? actionText : "View Details");

            // Process any remaining general placeholders in final HTML (e.g. if companyName
            // is in layout or footer)
            if (placeholders != null) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue() != null ? entry.getValue() : "";
                    finalHtml = finalHtml.replace("{{" + key + "}}", value);
                }
            }

            // 5. Send rich MIME message
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject(title);
            helper.setText(finalHtml, true); // true indicates HTML format

            mailSender.send(mimeMessage);
            log.info("Email Sent to: {}, Type: {}, Timestamp: {}", toEmail, templateName,
                    java.time.LocalDateTime.now());
        } catch (Exception e) {
            log.error("Email Failed to: {}, Type: {}, Timestamp: {}, Error: {}", toEmail, templateName,
                    java.time.LocalDateTime.now(), e.getMessage());
        }
    }

    private String loadTemplate(String name) {
        try (InputStream inputStream = getClass().getResourceAsStream("/templates/email/" + name);
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            if (inputStream == null) {
                log.error("Template stream not found for resource: /templates/email/{}", name);
                return null;
            }
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            log.error("Failed to load email template: {}", name, e);
            return null;
        }
    }

}