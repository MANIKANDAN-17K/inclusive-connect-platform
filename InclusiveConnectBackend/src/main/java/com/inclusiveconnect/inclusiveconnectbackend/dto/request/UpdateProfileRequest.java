package com.inclusiveconnect.inclusiveconnectbackend.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateProfileRequest {

    private String headline;
    private String location;

    @Size(max = 5000, message = "About section must be under 5000 characters")
    private String about;

    private String disabilityType;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
}