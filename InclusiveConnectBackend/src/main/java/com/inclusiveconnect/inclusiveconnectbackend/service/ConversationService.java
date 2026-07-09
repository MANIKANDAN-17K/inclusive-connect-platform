package com.inclusiveconnect.inclusiveconnectbackend.service;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.SendMessageRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ConversationResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.MessageResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Conversation;

import java.util.List;

public interface ConversationService {
    List<ConversationResponse> getMyConversations(Long userId);

    List<MessageResponse> getMessages(Long userId, Long conversationId);

    MessageResponse sendMessage(Long senderId, SendMessageRequest request);

    Conversation getOrCreateConversation(Long userAId, Long userBId);

    ConversationResponse toConversationResponse(Conversation conversation, Long currentUserId);
}