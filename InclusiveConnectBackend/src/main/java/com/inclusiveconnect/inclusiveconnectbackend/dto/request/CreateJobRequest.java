package com.inclusiveconnect.inclusiveconnectbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CreateJobRequest {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String location;

    @NotNull(message = "Employment type is required")
    private String employmentType; // FULL_TIME | PART_TIME | CONTRACT | INTERNSHIP | REMOTE

    private String salaryRange;
    private String experienceRequired;
    private String accessibilityNotes;
    private LocalDate applicationDeadline;
}