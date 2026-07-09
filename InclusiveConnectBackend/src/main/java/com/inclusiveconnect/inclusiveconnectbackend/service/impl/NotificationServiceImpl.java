package com.inclusiveconnect.inclusiveconnectbackend.service.impl;

import com.inclusiveconnect.inclusiveconnectbackend.dto.response.NotificationResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Notification;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.exception.UserNotFoundException;
import com.inclusiveconnect.inclusiveconnectbackend.repository.NotificationRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.UserRepository;
import com.inclusiveconnect.inclusiveconnectbackend.service.NotificationService;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    private final com.inclusiveconnect.inclusiveconnectbackend.service.EmailService emailService;

    @org.springframework.beans.factory.annotation.Value("${app.frontend-url:http://localhost:4200}")
    private String frontendUrl;

    public NotificationServiceImpl(NotificationRepository notificationRepository,
            UserRepository userRepository,
            SimpMessagingTemplate messagingTemplate,
            com.inclusiveconnect.inclusiveconnectbackend.service.EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
        this.emailService = emailService;
    }

    @Override
    @Transactional
    public List<NotificationResponse> getMyNotifications(Long userId) {
        return notificationRepository.findByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUser_IdAndIsReadFalse(userId);
    }

    @Override
    @Transactional
    public NotificationResponse markAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findByIdAndUser_Id(notificationId, userId)
                .orElseThrow(() -> new IllegalArgumentException("Notification not found"));

        notification.setRead(true);
        return toResponse(notificationRepository.save(notification));
    }

    @Override
    @Transactional
    public void createAndPush(Long userId, String title, String message,
            Notification.NotificationType type, String linkUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .linkUrl(linkUrl)
                .build();

        Notification saved = notificationRepository.save(notification);
        NotificationResponse response = toResponse(saved);

        // Push in real time to this specific user, if they're currently connected.
        try {
            messagingTemplate.convertAndSendToUser(
                    user.getEmail(),
                    "/queue/notifications",
                    response);
        } catch (Exception e) {
            // Log or ignore WebSocket push failures
        }

        // Email notification dispatch (wrapped in try-catch so it never rolls back
        // business database transactions)
        try {
            sendEmailNotificationIfApplicable(user, title, message, type, linkUrl);
        } catch (Exception e) {
            System.err.println("Failed to dispatch email notification: " + e.getMessage());
        }
    }

    private void sendEmailNotificationIfApplicable(User user, String title, String message,
            Notification.NotificationType type, String linkUrl) {

        String templateName = null;
        String actionText = "View Details";
        java.util.Map<String, String> placeholders = new java.util.HashMap<>();

        placeholders.put("userName", user.getFirstName());
        placeholders.put("actionUrl", frontendUrl + linkUrl);

        switch (type) {
            case CONNECTION_REQUEST:
                templateName = "connection-request.html";
                actionText = "Review Connection Request";
                placeholders.put("senderName", safeExtract(message, null, " wants to connect"));
                break;

            case CONNECTION_ACCEPTED:
                templateName = "connection-accepted.html";
                actionText = "View Profile";
                placeholders.put("senderName", safeExtract(message, null, " accepted your connection"));
                break;

            case JOB_APPLICATION_SUBMITTED:
                templateName = "job-application.html";
                actionText = "View My Applications";
                placeholders.put("jobTitle", safeExtract(message, "Application for ", " at "));
                placeholders.put("companyName", safeExtract(message, " at ", " submitted"));
                break;

            case NEW_JOB_APPLICATION:
                templateName = "job-application.html";
                actionText = "Review Candidate Applications";
                placeholders.put("senderName", safeExtract(message, null, " applied for "));
                placeholders.put("jobTitle", safeExtract(message, " applied for ", " at "));
                placeholders.put("companyName", safeExtract(message, " at ", null));
                break;

            case APPLICATION_SHORTLISTED:
                templateName = "application-shortlisted.html";
                actionText = "Check Application Status";
                placeholders.put("jobTitle", safeExtract(message, "Shortlisted for ", " at "));
                placeholders.put("companyName", safeExtract(message, " at ", null));
                break;

            case APPLICATION_REJECTED:
                templateName = "application-rejected.html";
                actionText = "Check Application Status";
                placeholders.put("jobTitle", safeExtract(message, "Application update for ", " at "));
                placeholders.put("companyName", safeExtract(message, " at ", null));
                break;

            case JOB_OFFER:
                templateName = "job-offer.html";
                actionText = "View Offer Details";
                placeholders.put("jobTitle", safeExtract(message, "Job offer for ", " at "));
                placeholders.put("companyName", safeExtract(message, " at ", null));
                break;

            case COMPANY_VERIFIED:
                templateName = "company-verified.html";
                actionText = "Go to Employer Profile";
                placeholders.put("companyName", safeExtract(message, "Company ", " has been"));
                break;

            default:
                // MESSAGE/SYSTEM or other types don't trigger emails
                return;
        }

        emailService.sendHtmlNotificationEmail(
                user.getEmail(),
                user.getFirstName(),
                title,
                frontendUrl + linkUrl,
                actionText,
                templateName,
                placeholders);
    }

    private String safeExtract(String text, String startToken, String endToken) {
        if (text == null)
            return "";
        int startIdx = 0;
        if (startToken != null) {
            int foundIdx = text.indexOf(startToken);
            if (foundIdx == -1)
                return "";
            startIdx = foundIdx + startToken.length();
        }
        int endIdx = text.length();
        if (endToken != null) {
            int foundIdx = text.indexOf(endToken, startIdx);
            if (foundIdx != -1) {
                endIdx = foundIdx;
            }
        }
        return text.substring(startIdx, endIdx).trim();
    }

    private NotificationResponse toResponse(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .title(n.getTitle())
                .message(n.getMessage())
                .type(n.getType().name())
                .isRead(n.isRead())
                .linkUrl(n.getLinkUrl())
                .createdAt(n.getCreatedAt())
                .build();
    }
}