package com.inclusiveconnect.inclusiveconnectbackend.websocket;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.SendMessageRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.MessageResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.service.ConversationService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
public class ChatWebSocketController {

    private final ConversationService conversationService;
    private final SimpMessagingTemplate messagingTemplate;

    public ChatWebSocketController(ConversationService conversationService,
                                   SimpMessagingTemplate messagingTemplate) {
        this.conversationService = conversationService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/chat.send")
    public void sendMessage(SendMessageRequest request, Authentication authentication) {
        User sender = (User) authentication.getPrincipal();

        MessageResponse saved = conversationService.sendMessage(sender.getId(), request);

        messagingTemplate.convertAndSend(
                "/topic/conversation." + saved.getConversationId(),
                saved
        );
    }
}