package com.inclusiveconnect.inclusiveconnectbackend.service.impl;

import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ConnectionRequestResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ConnectionResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.ConnectionRequest;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Notification;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.exception.ConnectionAlreadyExistsException;
import com.inclusiveconnect.inclusiveconnectbackend.exception.UserNotFoundException;
import com.inclusiveconnect.inclusiveconnectbackend.repository.ConnectionRequestRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.UserRepository;
import com.inclusiveconnect.inclusiveconnectbackend.service.ConnectionService;
import com.inclusiveconnect.inclusiveconnectbackend.service.NotificationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {

        private final ConnectionRequestRepository connectionRequestRepository;
        private final UserRepository userRepository;

        private final NotificationService notificationService;

        public ConnectionServiceImpl(ConnectionRequestRepository connectionRequestRepository,
                        UserRepository userRepository,
                        NotificationService notificationService) {
                this.connectionRequestRepository = connectionRequestRepository;
                this.userRepository = userRepository;
                this.notificationService = notificationService;
        }

        @Override
        @Transactional
        public ConnectionRequestResponse sendRequest(Long senderId, Long receiverId) {
                if (senderId.equals(receiverId)) {
                        throw new IllegalArgumentException("You can't send a connection request to yourself");
                }

                User sender = userRepository.findById(senderId)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));
                User receiver = userRepository.findById(receiverId)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                connectionRequestRepository.findBySender_IdAndReceiver_Id(senderId, receiverId)
                                .ifPresent(existing -> {
                                        throw new ConnectionAlreadyExistsException(
                                                        "You've already sent a request to this person");
                                });

                // If the other person already sent *us* a request, auto-accept theirs
                // instead of creating a confusing second pending request in the
                // opposite direction.
                var reverseRequest = connectionRequestRepository.findBySender_IdAndReceiver_Id(receiverId, senderId);
                if (reverseRequest.isPresent()
                                && reverseRequest.get().getStatus() == ConnectionRequest.ConnectionStatus.PENDING) {
                        ConnectionRequest reverse = reverseRequest.get();
                        reverse.setStatus(ConnectionRequest.ConnectionStatus.ACCEPTED);
                        reverse.setRespondedAt(LocalDateTime.now());
                        return toResponse(connectionRequestRepository.save(reverse));
                }

                ConnectionRequest request = ConnectionRequest.builder()
                                .sender(sender)
                                .receiver(receiver)
                                .status(ConnectionRequest.ConnectionStatus.PENDING)
                                .build();

                ConnectionRequest saved = connectionRequestRepository.save(request);

                notificationService.createAndPush(
                                receiverId,
                                "New connection request",
                                sender.getFirstName() + " " + sender.getLastName() + " wants to connect with you",
                                Notification.NotificationType.CONNECTION_REQUEST,
                                "/connections/pending");

                return toResponse(saved);
        }

        @Override
        @Transactional
        public ConnectionRequestResponse acceptRequest(Long receiverId, Long requestId) {
                ConnectionRequest request = connectionRequestRepository.findByIdAndReceiver_Id(requestId, receiverId)
                                .orElseThrow(() -> new IllegalArgumentException("Connection request not found"));

                request.setStatus(ConnectionRequest.ConnectionStatus.ACCEPTED);
                request.setRespondedAt(LocalDateTime.now());

                ConnectionRequest saved = connectionRequestRepository.save(request);

                notificationService.createAndPush(
                                request.getSender().getId(),
                                "Connection request accepted",
                                request.getReceiver().getFirstName() + " "
                                                + request.getReceiver().getLastName()
                                                + " accepted your connection request",
                                Notification.NotificationType.CONNECTION_ACCEPTED,
                                "/profile");

                return toResponse(saved);
        }

        @Override
        @Transactional
        public ConnectionRequestResponse rejectRequest(Long receiverId, Long requestId) {
                ConnectionRequest request = connectionRequestRepository.findByIdAndReceiver_Id(requestId, receiverId)
                                .orElseThrow(() -> new IllegalArgumentException("Connection request not found"));

                request.setStatus(ConnectionRequest.ConnectionStatus.REJECTED);
                request.setRespondedAt(LocalDateTime.now());
                notificationService.createAndPush(
                                request.getSender().getId(),
                                "Connection request declined",
                                request.getReceiver().getFirstName() + " "
                                                + request.getReceiver().getLastName()
                                                + " declined your connection request",
                                Notification.NotificationType.SYSTEM,
                                "/connections");

                return toResponse(connectionRequestRepository.save(request));
        }

        @Override
        @Transactional
        public List<ConnectionRequestResponse> getPendingRequests(Long userId) {
                return connectionRequestRepository
                                .findByReceiver_IdAndStatus(userId, ConnectionRequest.ConnectionStatus.PENDING).stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Override
        @Transactional
        public List<ConnectionRequestResponse> getSentRequests(Long userId) {
                return connectionRequestRepository
                                .findBySender_IdAndStatus(userId, ConnectionRequest.ConnectionStatus.PENDING).stream()
                                .map(this::toResponse)
                                .toList();
        }

        @Override
        @Transactional
        public void removeConnection(Long currentUserId, Long targetUserId) {
                ConnectionRequest connection = connectionRequestRepository
                                .findAcceptedBetween(currentUserId, targetUserId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "No active connection found with this user"));
                connectionRequestRepository.delete(connection);
        }

        @Override
        @Transactional
        public void cancelRequest(Long senderId, Long requestId) {
                ConnectionRequest request = connectionRequestRepository
                                .findByIdAndSender_Id(requestId, senderId)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Request not found or you are not the sender"));
                if (request.getStatus() != ConnectionRequest.ConnectionStatus.PENDING) {
                        throw new IllegalArgumentException("Only pending requests can be cancelled");
                }
                connectionRequestRepository.delete(request);
        }

        @Override
        @Transactional
        public List<ConnectionResponse> getMyConnections(Long userId) {
                return connectionRequestRepository.findAcceptedConnectionsForUser(userId).stream()
                                .map(cr -> {
                                        User other = cr.getSender().getId().equals(userId) ? cr.getReceiver()
                                                        : cr.getSender();
                                        return ConnectionResponse.builder()
                                                        .userId(other.getId())
                                                        .name(other.getFirstName() + " " + other.getLastName())
                                                        .headline(other.getHeadline())
                                                        .profilePicture(other.getProfilePicture())
                                                        .build();
                                })
                                .toList();
        }
        // constructor - add this parameter

        private ConnectionRequestResponse toResponse(ConnectionRequest cr) {
                return ConnectionRequestResponse.builder()
                                .id(cr.getId())
                                .status(cr.getStatus().name())
                                .sentAt(cr.getSentAt())
                                .senderId(cr.getSender().getId())
                                .senderName(cr.getSender().getFirstName() + " " + cr.getSender().getLastName())
                                .senderHeadline(cr.getSender().getHeadline())
                                .receiverId(cr.getReceiver().getId())
                                .receiverName(cr.getReceiver().getFirstName() + " " + cr.getReceiver().getLastName())
                                .receiverHeadline(cr.getReceiver().getHeadline())
                                .build();
        }

        // MapStruct note: same as ProfileServiceImpl — mapping is manual for now,
        // deliberately, while the mapping logic itself is still being learned.
}