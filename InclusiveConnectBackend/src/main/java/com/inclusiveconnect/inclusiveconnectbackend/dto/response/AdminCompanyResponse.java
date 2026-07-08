package com.inclusiveconnect.inclusiveconnectbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class AdminCompanyResponse {
    private Long id;
    private String companyName;
    private String industry;
    private boolean verified;
    private Long employerUserId;
    private String employerEmail;
}