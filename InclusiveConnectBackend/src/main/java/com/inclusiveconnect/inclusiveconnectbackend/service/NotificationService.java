package com.inclusiveconnect.inclusiveconnectbackend.service;

import com.inclusiveconnect.inclusiveconnectbackend.dto.response.NotificationResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Notification;

import java.util.List;

public interface NotificationService {
    List<NotificationResponse> getMyNotifications(Long userId);
    long getUnreadCount(Long userId);
    NotificationResponse markAsRead(Long userId, Long notificationId);
    void createAndPush(Long userId, String title, String message, Notification.NotificationType type, String linkUrl);
}