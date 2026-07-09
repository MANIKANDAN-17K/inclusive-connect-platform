package com.inclusiveconnect.inclusiveconnectbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class CompanyResponse {
    private Long id;
    private String companyName;
    private String website;
    private String industry;
    private String description;
    private String companySize;
    private String logoUrl;
    private String coverImageUrl;
    private boolean verified;
    private Long employerUserId;
}