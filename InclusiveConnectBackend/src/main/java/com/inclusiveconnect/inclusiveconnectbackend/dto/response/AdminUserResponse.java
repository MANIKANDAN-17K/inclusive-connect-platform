package com.inclusiveconnect.inclusiveconnectbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminUserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String role;
    private boolean isVerified;
    private boolean isActive;
}