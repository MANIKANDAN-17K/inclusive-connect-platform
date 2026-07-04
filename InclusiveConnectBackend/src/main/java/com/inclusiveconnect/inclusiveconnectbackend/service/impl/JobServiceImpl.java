package com.inclusiveconnect.inclusiveconnectbackend.service.impl;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.CreateJobRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.UpdateJobRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.JobResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.PageResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Company;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Job;
import com.inclusiveconnect.inclusiveconnectbackend.exception.JobNotFoundException;
import com.inclusiveconnect.inclusiveconnectbackend.repository.CompanyRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.JobRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.spec.JobSpecifications;
import com.inclusiveconnect.inclusiveconnectbackend.service.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    public JobServiceImpl(JobRepository jobRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
    }

    @Override
    @Transactional
    public JobResponse createJob(Long userId, CreateJobRequest request) {
        Company company = companyRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("You need to create a company profile before posting jobs"));

        Job job = Job.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .location(request.getLocation())
                .employmentType(parseEmploymentType(request.getEmploymentType()))
                .salaryRange(request.getSalaryRange())
                .experienceRequired(request.getExperienceRequired())
                .accessibilityNotes(request.getAccessibilityNotes())
                .applicationDeadline(request.getApplicationDeadline())
                .company(company)
                .build();

        return toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public JobResponse getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found"));
        return toResponse(job);
    }

    @Override

    @Transactional
    public List<JobResponse> getMyCompanyJobs(Long userId) {
        Company company = companyRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("You haven't created a company profile yet"));

        return jobRepository.findByCompany_Id(company.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public JobResponse updateJob(Long userId, Long jobId, UpdateJobRequest request) {
        Company company = companyRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("You haven't created a company profile yet"));

        Job job = jobRepository.findByIdAndCompany_Id(jobId, company.getId())
                .orElseThrow(() -> new JobNotFoundException("Job not found"));

        if (request.getTitle() != null) job.setTitle(request.getTitle());
        if (request.getDescription() != null) job.setDescription(request.getDescription());
        if (request.getLocation() != null) job.setLocation(request.getLocation());
        if (request.getEmploymentType() != null) job.setEmploymentType(parseEmploymentType(request.getEmploymentType()));
        if (request.getSalaryRange() != null) job.setSalaryRange(request.getSalaryRange());
        if (request.getExperienceRequired() != null) job.setExperienceRequired(request.getExperienceRequired());
        if (request.getAccessibilityNotes() != null) job.setAccessibilityNotes(request.getAccessibilityNotes());
        if (request.getApplicationDeadline() != null) job.setApplicationDeadline(request.getApplicationDeadline());
        if (request.getStatus() != null) job.setStatus(parseStatus(request.getStatus()));

        return toResponse(jobRepository.save(job));
    }

    @Override
    @Transactional
    public void deleteJob(Long userId, Long jobId) {
        Company company = companyRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("You haven't created a company profile yet"));

        Job job = jobRepository.findByIdAndCompany_Id(jobId, company.getId())
                .orElseThrow(() -> new JobNotFoundException("Job not found"));

        jobRepository.delete(job);
    }

    private Job.EmploymentType parseEmploymentType(String raw) {
        try {
            return Job.EmploymentType.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Employment type must be one of: FULL_TIME, PART_TIME, CONTRACT, INTERNSHIP, REMOTE");
        }
    }

    private Job.JobStatus parseStatus(String raw) {
        try {
            return Job.JobStatus.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status must be one of: OPEN, CLOSED, DRAFT");
        }
    }
    @Override
    @Transactional
    public PageResponse<JobResponse> searchJobs(String keyword, String location, String employmentType, int page, int size) {
        Specification<Job> spec = Specification.where(JobSpecifications.hasStatus(Job.JobStatus.OPEN));

        if (keyword != null && !keyword.isBlank()) {
            spec = spec.and(JobSpecifications.keywordMatches(keyword));
        }
        if (location != null && !location.isBlank()) {
            spec = spec.and(JobSpecifications.locationContains(location));
        }
        if (employmentType != null && !employmentType.isBlank()) {
            spec = spec.and(JobSpecifications.hasEmploymentType(parseEmploymentType(employmentType)));
        }

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Job> jobPage = jobRepository.findAll(spec, pageable);

        Page<JobResponse> responsePage = jobPage.map(this::toResponse);
        return PageResponse.from(responsePage);
    }
    private JobResponse toResponse(Job job) {
        Company company = job.getCompany();
        return JobResponse.builder()
                .id(job.getId())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .employmentType(job.getEmploymentType().name())
                .salaryRange(job.getSalaryRange())
                .experienceRequired(job.getExperienceRequired())
                .accessibilityNotes(job.getAccessibilityNotes())
                .applicationDeadline(job.getApplicationDeadline())
                .status(job.getStatus().name())
                .createdAt(job.getCreatedAt())
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .companyLogoUrl(company.getLogoUrl())
                .build();
    }
}