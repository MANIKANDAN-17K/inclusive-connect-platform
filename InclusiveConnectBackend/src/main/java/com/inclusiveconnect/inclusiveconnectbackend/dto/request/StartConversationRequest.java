package com.inclusiveconnect.inclusiveconnectbackend.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartConversationRequest {

    @NotNull(message = "receiverId is required")
    private Long receiverId;
}
