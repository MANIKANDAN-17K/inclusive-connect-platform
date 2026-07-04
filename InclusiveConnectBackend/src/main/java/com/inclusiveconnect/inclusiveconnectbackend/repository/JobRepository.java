package com.inclusiveconnect.inclusiveconnectbackend.repository;

import com.inclusiveconnect.inclusiveconnectbackend.entity.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {
    List<Job> findByCompany_Id(Long companyId);
    Optional<Job> findByIdAndCompany_Id(Long jobId, Long companyId);
}