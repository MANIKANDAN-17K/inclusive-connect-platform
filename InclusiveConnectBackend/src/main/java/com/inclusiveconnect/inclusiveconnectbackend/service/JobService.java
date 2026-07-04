package com.inclusiveconnect.inclusiveconnectbackend.service;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.CreateJobRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.UpdateJobRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.JobResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.PageResponse;

import java.util.List;

public interface JobService {
    JobResponse createJob(Long userId, CreateJobRequest request);
    JobResponse getJobById(Long jobId);
    List<JobResponse> getMyCompanyJobs(Long userId);
    JobResponse updateJob(Long userId, Long jobId, UpdateJobRequest request);
    void deleteJob(Long userId, Long jobId);

    PageResponse<JobResponse> searchJobs(String keyword, String location, String employmentType, int page, int size);
}