package com.inclusiveconnect.inclusiveconnectbackend.service.impl;

import com.inclusiveconnect.inclusiveconnectbackend.dto.response.NetworkUserResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.repository.ConnectionRequestRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.UserRepository;
import com.inclusiveconnect.inclusiveconnectbackend.service.NetworkService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NetworkServiceImpl implements NetworkService {

    private final UserRepository userRepository;
    private final ConnectionRequestRepository connectionRequestRepository;

    public NetworkServiceImpl(UserRepository userRepository,
            ConnectionRequestRepository connectionRequestRepository) {
        this.userRepository = userRepository;
        this.connectionRequestRepository = connectionRequestRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NetworkUserResponse> discoverPeople(Long currentUserId, Pageable pageable) {
        return userRepository.findDiscoverableUsers(currentUserId, pageable)
                .map(user -> toNetworkUserResponse(user, currentUserId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NetworkUserResponse> searchUsers(Long currentUserId, String keyword, Pageable pageable) {
        if (keyword == null || keyword.isBlank()) {
            return discoverPeople(currentUserId, pageable);
        }
        return userRepository.searchUsers(currentUserId, keyword.trim(), pageable)
                .map(user -> toNetworkUserResponse(user, currentUserId));
    }

    private NetworkUserResponse toNetworkUserResponse(User user, Long currentUserId) {
        boolean connected = connectionRequestRepository.hasAcceptedBetween(currentUserId, user.getId());
        boolean pending = connectionRequestRepository.hasPendingBetween(currentUserId, user.getId());

        return NetworkUserResponse.builder()
                .id(user.getId())
                .fullName(user.getFirstName() + " " + user.getLastName())
                .headline(user.getHeadline())
                .profilePicture(user.getProfilePicture())
                .location(user.getLocation())
                .role(user.getRole() != null ? user.getRole().getName().name() : null)
                .mutualConnections(0) // placeholder
                .alreadyConnected(connected)
                .requestPending(pending)
                .build();
    }
}
