package com.inclusiveconnect.inclusiveconnectbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SendMessageRequest {

    @NotBlank(message = "Message content is required")
    private String content;

    private Long conversationId; // if null, backend creates/finds the conversation using receiverId instead

    private Long receiverId; // used only when conversationId is null (starting a brand-new conversation)
}