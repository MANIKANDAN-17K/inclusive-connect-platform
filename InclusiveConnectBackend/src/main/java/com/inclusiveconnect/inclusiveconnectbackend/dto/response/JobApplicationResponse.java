package com.inclusiveconnect.inclusiveconnectbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
public class JobApplicationResponse {
    private Long id;
    private String status;
    private String coverLetter;
    private String resumeUrl;
    private LocalDateTime appliedAt;

    private Long jobId;
    private String jobTitle;
    private String companyName;

    private Long candidateId;
    private String candidateName;
    private String candidateEmail;
}