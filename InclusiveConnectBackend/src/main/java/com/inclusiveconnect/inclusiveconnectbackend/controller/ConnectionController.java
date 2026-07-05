package com.inclusiveconnect.inclusiveconnectbackend.controller;

import com.inclusiveconnect.inclusiveconnectbackend.common.ApiResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ConnectionRequestResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ConnectionResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.service.ConnectionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/connections")
@Tag(name = "Connections", description = "Sending, accepting, and managing professional connections")
public class ConnectionController {

    private final ConnectionService connectionService;

    public ConnectionController(ConnectionService connectionService) {
        this.connectionService = connectionService;
    }

    @PostMapping("/request/{userId}")
    public ResponseEntity<ApiResponse<ConnectionRequestResponse>> sendRequest(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long userId) {
        ConnectionRequestResponse response = connectionService.sendRequest(currentUser.getId(), userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Connection request sent", response));
    }

    @PutMapping("/accept/{requestId}")
    public ResponseEntity<ApiResponse<ConnectionRequestResponse>> acceptRequest(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long requestId) {
        ConnectionRequestResponse response = connectionService.acceptRequest(currentUser.getId(), requestId);
        return ResponseEntity.ok(ApiResponse.success("Connection request accepted", response));
    }

    @PutMapping("/reject/{requestId}")
    public ResponseEntity<ApiResponse<ConnectionRequestResponse>> rejectRequest(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long requestId) {
        ConnectionRequestResponse response = connectionService.rejectRequest(currentUser.getId(), requestId);
        return ResponseEntity.ok(ApiResponse.success("Connection request rejected", response));
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<ConnectionRequestResponse>>> getPendingRequests(
            @AuthenticationPrincipal User currentUser) {
        List<ConnectionRequestResponse> response = connectionService.getPendingRequests(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Pending requests fetched", response));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ConnectionResponse>>> getMyConnections(
            @AuthenticationPrincipal User currentUser) {
        List<ConnectionResponse> response = connectionService.getMyConnections(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Connections fetched", response));
    }
}