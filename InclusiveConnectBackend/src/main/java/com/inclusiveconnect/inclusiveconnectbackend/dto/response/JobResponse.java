package com.inclusiveconnect.inclusiveconnectbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class JobResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private String employmentType;
    private String salaryRange;
    private String experienceRequired;
    private String accessibilityNotes;
    private LocalDate applicationDeadline;
    private String status;
    private LocalDateTime createdAt;

    private Long companyId;
    private String companyName;
    private String companyLogoUrl;
}