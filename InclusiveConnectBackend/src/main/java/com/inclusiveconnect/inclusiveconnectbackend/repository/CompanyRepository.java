package com.inclusiveconnect.inclusiveconnectbackend.repository;

import com.inclusiveconnect.inclusiveconnectbackend.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByUser_Id(Long userId);
    boolean existsByUser_Id(Long userId);
    List<Company> findByVerifiedFalse();
    long countByVerifiedFalse();
}