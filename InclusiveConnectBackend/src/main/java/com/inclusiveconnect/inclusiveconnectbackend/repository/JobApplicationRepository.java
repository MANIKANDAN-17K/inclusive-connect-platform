package com.inclusiveconnect.inclusiveconnectbackend.repository;

import com.inclusiveconnect.inclusiveconnectbackend.entity.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByCandidate_Id(Long candidateId);
    List<JobApplication> findByJob_Id(Long jobId);
    Optional<JobApplication> findByIdAndJob_Company_User_Id(Long applicationId, Long employerUserId);
    boolean existsByJob_IdAndCandidate_Id(Long jobId, Long candidateId);
}