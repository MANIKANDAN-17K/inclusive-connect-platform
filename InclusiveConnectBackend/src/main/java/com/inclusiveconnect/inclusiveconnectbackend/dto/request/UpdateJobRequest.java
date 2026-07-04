package com.inclusiveconnect.inclusiveconnectbackend.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class UpdateJobRequest {
    private String title;
    private String description;
    private String location;
    private String employmentType;
    private String salaryRange;
    private String experienceRequired;
    private String accessibilityNotes;
    private LocalDate applicationDeadline;
    private String status; // OPEN | CLOSED | DRAFT
}