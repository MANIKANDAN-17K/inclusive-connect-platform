package com.inclusiveconnect.inclusiveconnectbackend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EducationRequest {

    @NotBlank(message = "Institution name is required")
    private String institutionName;

    @NotBlank(message = "Degree is required")
    private String degree;

    private String fieldOfStudy;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double cgpa;
    private String description;
}