package com.inclusiveconnect.inclusiveconnectbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ConnectionResponse {
    private Long userId;
    private String name;
    private String headline;
    private String profilePicture;
}