package com.inclusiveconnect.inclusiveconnectbackend.repository.spec;

import com.inclusiveconnect.inclusiveconnectbackend.entity.Job;
import org.springframework.data.jpa.domain.Specification;

public class JobSpecifications {

    private JobSpecifications() {
        // utility class — never instantiated
    }

    public static Specification<Job> hasStatus(Job.JobStatus status) {
        return (root, query, cb) -> cb.equal(root.get("status"), status);
    }

    public static Specification<Job> keywordMatches(String keyword) {
        String likePattern = "%" + keyword.toLowerCase() + "%";
        return (root, query, cb) -> cb.or(
                cb.like(cb.lower(root.get("title")), likePattern),
                cb.like(cb.lower(root.get("description")), likePattern)
        );
    }

    public static Specification<Job> locationContains(String location) {
        String likePattern = "%" + location.toLowerCase() + "%";
        return (root, query, cb) -> cb.like(cb.lower(root.get("location")), likePattern);
    }

    public static Specification<Job> hasEmploymentType(Job.EmploymentType employmentType) {
        return (root, query, cb) -> cb.equal(root.get("employmentType"), employmentType);
    }
}