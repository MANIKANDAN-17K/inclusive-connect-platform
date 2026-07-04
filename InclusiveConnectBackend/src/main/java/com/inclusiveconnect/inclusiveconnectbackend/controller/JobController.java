package com.inclusiveconnect.inclusiveconnectbackend.controller;

import com.inclusiveconnect.inclusiveconnectbackend.common.ApiResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.CreateJobRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.UpdateJobRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.JobResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.PageResponse;
import org.springframework.web.bind.annotation.RequestParam;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.service.JobService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/jobs")
@Tag(name = "Jobs", description = "Job posting management")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateJobRequest request) {
        JobResponse response = jobService.createJob(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Job posted", response));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {
        JobResponse response = jobService.getJobById(id);
        return ResponseEntity.ok(ApiResponse.success("Job fetched", response));
    }

    @GetMapping("/my-jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getMyCompanyJobs(@AuthenticationPrincipal User currentUser) {
        List<JobResponse> response = jobService.getMyCompanyJobs(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Jobs fetched", response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<JobResponse>> updateJob(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody UpdateJobRequest request) {
        JobResponse response = jobService.updateJob(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Job updated", response));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteJob(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        jobService.deleteJob(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Job deleted"));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<JobResponse>>> searchJobs(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String employmentType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        PageResponse<JobResponse> response = jobService.searchJobs(keyword, location, employmentType, page, size);
        return ResponseEntity.ok(ApiResponse.success("Jobs fetched", response));
    }
}