package com.inclusiveconnect.inclusiveconnectbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class ConnectionRequestResponse {
    private Long id;
    private String status;
    private LocalDateTime sentAt;

    private Long senderId;
    private String senderName;
    private String senderHeadline;

    private Long receiverId;
    private String receiverName;
    private String receiverHeadline;
}