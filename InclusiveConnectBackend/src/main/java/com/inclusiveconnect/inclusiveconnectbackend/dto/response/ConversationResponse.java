package com.inclusiveconnect.inclusiveconnectbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ConversationResponse {
    private Long id;
    private LocalDateTime createdAt;

    private Long otherUserId;
    private String otherUserName;
    private String otherUserProfilePicture;

    private String lastMessagePreview;
    private LocalDateTime lastMessageAt;
}