package com.inclusiveconnect.inclusiveconnectbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class NetworkUserResponse {
    private Long id;
    private String fullName;
    private String headline;
    private String profilePicture;
    private String location;
    private String role;
    private int mutualConnections; // placeholder – 0 for now
    private boolean alreadyConnected;
    private boolean requestPending;
}
