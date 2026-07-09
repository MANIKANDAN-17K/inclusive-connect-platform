package com.inclusiveconnect.inclusiveconnectbackend.service;

import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ConnectionRequestResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ConnectionResponse;

import java.util.List;

public interface ConnectionService {
    ConnectionRequestResponse sendRequest(Long senderId, Long receiverId);

    ConnectionRequestResponse acceptRequest(Long receiverId, Long requestId);

    ConnectionRequestResponse rejectRequest(Long receiverId, Long requestId);

    List<ConnectionRequestResponse> getPendingRequests(Long userId);

    List<ConnectionRequestResponse> getSentRequests(Long userId);

    List<ConnectionResponse> getMyConnections(Long userId);

    void removeConnection(Long currentUserId, Long targetUserId);

    void cancelRequest(Long senderId, Long requestId);
}