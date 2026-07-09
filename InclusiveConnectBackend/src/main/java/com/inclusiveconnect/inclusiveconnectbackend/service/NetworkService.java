package com.inclusiveconnect.inclusiveconnectbackend.service;

import com.inclusiveconnect.inclusiveconnectbackend.dto.response.NetworkUserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NetworkService {
    Page<NetworkUserResponse> discoverPeople(Long currentUserId, Pageable pageable);

    Page<NetworkUserResponse> searchUsers(Long currentUserId, String keyword, Pageable pageable);
}
