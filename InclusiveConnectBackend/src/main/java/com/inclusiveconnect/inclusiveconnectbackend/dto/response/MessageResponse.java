package com.inclusiveconnect.inclusiveconnectbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class MessageResponse {
    private Long id;
    private Long conversationId;
    private String content;
    private boolean isRead;
    private LocalDateTime sentAt;

    private Long senderId;
    private String senderName;
}