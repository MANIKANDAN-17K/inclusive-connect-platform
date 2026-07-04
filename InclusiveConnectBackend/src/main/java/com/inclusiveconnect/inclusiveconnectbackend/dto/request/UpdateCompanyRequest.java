package com.inclusiveconnect.inclusiveconnectbackend.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateCompanyRequest {
    private String companyName;
    private String website;
    private String industry;
    private String description;
    private String companySize;
}