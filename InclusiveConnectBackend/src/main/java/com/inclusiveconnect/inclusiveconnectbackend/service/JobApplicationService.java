package com.inclusiveconnect.inclusiveconnectbackend.service;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.ApplyJobRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.UpdateApplicationStatusRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.JobApplicationResponse;

import java.util.List;

public interface JobApplicationService {
    JobApplicationResponse applyToJob(Long candidateUserId, Long jobId, ApplyJobRequest request);
    List<JobApplicationResponse> getMyApplications(Long candidateUserId);
    List<JobApplicationResponse> getApplicationsForJob(Long employerUserId, Long jobId);
    JobApplicationResponse updateApplicationStatus(Long employerUserId, Long applicationId, UpdateApplicationStatusRequest request);
}