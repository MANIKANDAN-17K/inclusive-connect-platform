package com.inclusiveconnect.inclusiveconnectbackend.controller;

import com.inclusiveconnect.inclusiveconnectbackend.common.ApiResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.ApplyJobRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.UpdateApplicationStatusRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.JobApplicationResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.service.JobApplicationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "Job Applications", description = "Applying to jobs and managing applicants")
public class JobApplicationController {

    private final JobApplicationService applicationService;

    public JobApplicationController(JobApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping("/api/v1/jobs/{jobId}/apply")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> applyToJob(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long jobId,
            @RequestBody(required = false) ApplyJobRequest request) {
        ApplyJobRequest safeRequest = request != null ? request : new ApplyJobRequest();
        JobApplicationResponse response = applicationService.applyToJob(currentUser.getId(), jobId, safeRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Application submitted", response));
    }

    @GetMapping("/api/v1/applications/me")
    public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getMyApplications(@AuthenticationPrincipal User currentUser) {
        List<JobApplicationResponse> response = applicationService.getMyApplications(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Applications fetched", response));
    }

    @GetMapping("/api/v1/jobs/{jobId}/applications")
    public ResponseEntity<ApiResponse<List<JobApplicationResponse>>> getApplicationsForJob(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long jobId) {
        List<JobApplicationResponse> response = applicationService.getApplicationsForJob(currentUser.getId(), jobId);
        return ResponseEntity.ok(ApiResponse.success("Applicants fetched", response));
    }

    @PatchMapping("/api/v1/applications/{id}/status")
    public ResponseEntity<ApiResponse<JobApplicationResponse>> updateStatus(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody UpdateApplicationStatusRequest request) {
        JobApplicationResponse response = applicationService.updateApplicationStatus(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Application status updated", response));
    }
}