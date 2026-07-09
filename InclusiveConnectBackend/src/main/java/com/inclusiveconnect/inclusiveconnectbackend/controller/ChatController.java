package com.inclusiveconnect.inclusiveconnectbackend.controller;

import com.inclusiveconnect.inclusiveconnectbackend.common.ApiResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.StartConversationRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ConversationResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.MessageResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Conversation;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.service.ConversationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/chat")
@Tag(name = "Chat", description = "Conversation history (real-time sending is via WebSocket /ws)")
public class ChatController {

    private final ConversationService conversationService;

    public ChatController(ConversationService conversationService) {
        this.conversationService = conversationService;
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> getMyConversations(
            @AuthenticationPrincipal User currentUser) {
        List<ConversationResponse> response = conversationService.getMyConversations(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Conversations fetched", response));
    }

    @PostMapping("/conversations/start")
    public ResponseEntity<ApiResponse<ConversationResponse>> startConversation(
            @AuthenticationPrincipal User currentUser,
            @RequestBody StartConversationRequest request) {
        Conversation conv = conversationService.getOrCreateConversation(currentUser.getId(), request.getReceiverId());
        ConversationResponse response = conversationService.toConversationResponse(conv, currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Conversation ready", response));
    }

    @GetMapping("/conversations/{id}/messages")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        List<MessageResponse> response = conversationService.getMessages(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Messages fetched", response));
    }
}