package com.inclusiveconnect.inclusiveconnectbackend.service.impl;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.ApplyJobRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.UpdateApplicationStatusRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.JobApplicationResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Job;
import com.inclusiveconnect.inclusiveconnectbackend.entity.JobApplication;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Profile;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.exception.JobNotFoundException;
import com.inclusiveconnect.inclusiveconnectbackend.repository.JobApplicationRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.JobRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.ProfileRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.UserRepository;
import com.inclusiveconnect.inclusiveconnectbackend.service.JobApplicationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class JobApplicationServiceImpl implements JobApplicationService {

    private final JobApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final UserRepository userRepository;
    private final ProfileRepository profileRepository;

    public JobApplicationServiceImpl(JobApplicationRepository applicationRepository,
                                     JobRepository jobRepository,
                                     UserRepository userRepository,
                                     ProfileRepository profileRepository) {
        this.applicationRepository = applicationRepository;
        this.jobRepository = jobRepository;
        this.userRepository = userRepository;
        this.profileRepository = profileRepository;
    }

    @Override
    @Transactional
    public JobApplicationResponse applyToJob(Long candidateUserId, Long jobId, ApplyJobRequest request) {
        User candidate = userRepository.findById(candidateUserId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found"));

        if (job.getStatus() != Job.JobStatus.OPEN) {
            throw new IllegalArgumentException("This job is no longer accepting applications");
        }

        if (applicationRepository.existsByJob_IdAndCandidate_Id(jobId, candidateUserId)) {
            throw new IllegalArgumentException("You've already applied to this job");
        }

        String resumeUrl = profileRepository.findByUser_Id(candidateUserId)
                .map(Profile::getResumeUrl)
                .orElse(null);

        JobApplication application = JobApplication.builder()
                .candidate(candidate)
                .job(job)
                .coverLetter(request.getCoverLetter())
                .resumeUrl(resumeUrl)
                .build();

        return toResponse(applicationRepository.save(application));
    }

    @Override
    @Transactional
    public List<JobApplicationResponse> getMyApplications(Long candidateUserId) {
        return applicationRepository.findByCandidate_Id(candidateUserId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public List<JobApplicationResponse> getApplicationsForJob(Long employerUserId, Long jobId) {
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new JobNotFoundException("Job not found"));

        if (!job.getCompany().getUser().getId().equals(employerUserId)) {
            throw new IllegalArgumentException("You don't have access to this job's applications");
        }

        return applicationRepository.findByJob_Id(jobId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public JobApplicationResponse updateApplicationStatus(Long employerUserId, Long applicationId, UpdateApplicationStatusRequest request) {
        JobApplication application = applicationRepository.findByIdAndJob_Company_User_Id(applicationId, employerUserId)
                .orElseThrow(() -> new IllegalArgumentException("Application not found"));

        application.setStatus(parseStatus(request.getStatus()));
        return toResponse(applicationRepository.save(application));
    }

    private JobApplication.ApplicationStatus parseStatus(String raw) {
        try {
            return JobApplication.ApplicationStatus.valueOf(raw.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Status must be one of: PENDING, SHORTLISTED, REJECTED, HIRED");
        }
    }

    private JobApplicationResponse toResponse(JobApplication app) {
        return JobApplicationResponse.builder()
                .id(app.getId())
                .status(app.getStatus().name())
                .coverLetter(app.getCoverLetter())
                .resumeUrl(app.getResumeUrl())
                .appliedAt(app.getAppliedAt())
                .jobId(app.getJob().getId())
                .jobTitle(app.getJob().getTitle())
                .companyName(app.getJob().getCompany().getCompanyName())
                .candidateId(app.getCandidate().getId())
                .candidateName(app.getCandidate().getFirstName() + " " + app.getCandidate().getLastName())
                .candidateEmail(app.getCandidate().getEmail())
                .build();
    }
}