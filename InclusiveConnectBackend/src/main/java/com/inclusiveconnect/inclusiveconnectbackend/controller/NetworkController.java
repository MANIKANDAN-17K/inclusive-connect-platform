package com.inclusiveconnect.inclusiveconnectbackend.controller;

import com.inclusiveconnect.inclusiveconnectbackend.common.ApiResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.NetworkUserResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.service.NetworkService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/network")
@Tag(name = "Network", description = "People discovery and search")
public class NetworkController {

    private final NetworkService networkService;

    public NetworkController(NetworkService networkService) {
        this.networkService = networkService;
    }

    @GetMapping("/discover")
    public ResponseEntity<ApiResponse<Page<NetworkUserResponse>>> discoverPeople(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<NetworkUserResponse> result = networkService.discoverPeople(currentUser.getId(), pageable);
        return ResponseEntity.ok(ApiResponse.success("People discovered", result));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<NetworkUserResponse>>> searchUsers(
            @AuthenticationPrincipal User currentUser,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        PageRequest pageable = PageRequest.of(page, size);
        Page<NetworkUserResponse> result = networkService.searchUsers(currentUser.getId(), keyword, pageable);
        return ResponseEntity.ok(ApiResponse.success("Search results", result));
    }
}
