package com.inclusiveconnect.inclusiveconnectbackend.controller;

import com.inclusiveconnect.inclusiveconnectbackend.common.ApiResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.NotificationResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "In-app and real-time notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal User currentUser) {
        List<NotificationResponse> response = notificationService.getMyNotifications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Notifications fetched", response));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal User currentUser) {
        long count = notificationService.getUnreadCount(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Unread count fetched", Map.of("count", count)));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> markAsRead(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        NotificationResponse response = notificationService.markAsRead(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read", response));
    }
}