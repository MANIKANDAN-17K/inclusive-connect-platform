package com.inclusiveconnect.inclusiveconnectbackend.service.impl;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.SendMessageRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ConversationResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.MessageResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Conversation;
import com.inclusiveconnect.inclusiveconnectbackend.entity.ConnectionRequest;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Message;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.exception.UnauthorizedException;
import com.inclusiveconnect.inclusiveconnectbackend.exception.UserNotFoundException;
import com.inclusiveconnect.inclusiveconnectbackend.repository.ConnectionRequestRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.ConversationRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.MessageRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.UserRepository;
import com.inclusiveconnect.inclusiveconnectbackend.service.ConversationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

@Service
public class ConversationServiceImpl implements ConversationService {

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConnectionRequestRepository connectionRequestRepository;

    public ConversationServiceImpl(ConversationRepository conversationRepository,
                                   MessageRepository messageRepository,
                                   UserRepository userRepository,
                                   ConnectionRequestRepository connectionRequestRepository) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.connectionRequestRepository = connectionRequestRepository;
    }

    @Override
    @Transactional
    public List<ConversationResponse> getMyConversations(Long userId) {
        return conversationRepository.findAllForUser(userId).stream()
                .map(conv -> toConversationResponse(conv, userId))
                .sorted(Comparator.comparing(
                        ConversationResponse::getLastMessageAt,
                        Comparator.nullsLast(Comparator.reverseOrder())))
                .toList();
    }

    @Override
    @Transactional
    public List<MessageResponse> getMessages(Long userId, Long conversationId) {
        Conversation conversation = conversationRepository.findById(conversationId)
                .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(userId));
        if (!isParticipant) {
            throw new UnauthorizedException("You don't have access to this conversation");
        }

        return messageRepository.findByConversation_IdOrderBySentAtAsc(conversationId).stream()
                .map(this::toMessageResponse)
                .toList();
    }

    @Override
    @Transactional
    public MessageResponse sendMessage(Long senderId, SendMessageRequest request) {
        Conversation conversation;

        if (request.getConversationId() != null) {
            conversation = conversationRepository.findById(request.getConversationId())
                    .orElseThrow(() -> new IllegalArgumentException("Conversation not found"));

            boolean isParticipant = conversation.getParticipants().stream()
                    .anyMatch(p -> p.getId().equals(senderId));
            if (!isParticipant) {
                throw new UnauthorizedException("You don't have access to this conversation");
            }
        } else {
            if (request.getReceiverId() == null) {
                throw new IllegalArgumentException("Either conversationId or receiverId is required");
            }
            conversation = getOrCreateConversation(senderId, request.getReceiverId());
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Message message = Message.builder()
                .content(request.getContent())
                .sender(sender)
                .conversation(conversation)
                .build();

        return toMessageResponse(messageRepository.save(message));
    }

    @Override
    @Transactional
    public Conversation getOrCreateConversation(Long userAId, Long userBId) {
        if (userAId.equals(userBId)) {
            throw new IllegalArgumentException("You can't start a conversation with yourself");
        }

        boolean areConnected = connectionRequestRepository.findBySender_IdAndReceiver_Id(userAId, userBId)
                .filter(cr -> cr.getStatus() == ConnectionRequest.ConnectionStatus.ACCEPTED)
                .isPresent()
                ||
                connectionRequestRepository.findBySender_IdAndReceiver_Id(userBId, userAId)
                        .filter(cr -> cr.getStatus() == ConnectionRequest.ConnectionStatus.ACCEPTED)
                        .isPresent();

        if (!areConnected) {
            throw new UnauthorizedException("You can only message users you're connected with");
        }

        return conversationRepository.findConversationBetween(userAId, userBId)
                .orElseGet(() -> {
                    User userA = userRepository.findById(userAId)
                            .orElseThrow(() -> new UserNotFoundException("User not found"));
                    User userB = userRepository.findById(userBId)
                            .orElseThrow(() -> new UserNotFoundException("User not found"));

                    Conversation conversation = Conversation.builder()
                            .participants(Set.of(userA, userB))
                            .build();

                    return conversationRepository.save(conversation);
                });
    }

    private ConversationResponse toConversationResponse(Conversation conversation, Long currentUserId) {
        User other = conversation.getParticipants().stream()
                .filter(p -> !p.getId().equals(currentUserId))
                .findFirst()
                .orElse(null);

        List<Message> messages = messageRepository.findByConversation_IdOrderBySentAtAsc(conversation.getId());
        Message lastMessage = messages.isEmpty() ? null : messages.get(messages.size() - 1);

        return ConversationResponse.builder()
                .id(conversation.getId())
                .createdAt(conversation.getCreatedAt())
                .otherUserId(other != null ? other.getId() : null)
                .otherUserName(other != null ? other.getFirstName() + " " + other.getLastName() : "Unknown")
                .otherUserProfilePicture(other != null ? other.getProfilePicture() : null)
                .lastMessagePreview(lastMessage != null ? lastMessage.getContent() : null)
                .lastMessageAt(lastMessage != null ? lastMessage.getSentAt() : null)
                .build();
    }

    private MessageResponse toMessageResponse(Message message) {
        return MessageResponse.builder()
                .id(message.getId())
                .conversationId(message.getConversation().getId())
                .content(message.getContent())
                .isRead(message.isRead())
                .sentAt(message.getSentAt())
                .senderId(message.getSender().getId())
                .senderName(message.getSender().getFirstName() + " " + message.getSender().getLastName())
                .build();
    }
}