package com.inclusiveconnect.inclusiveconnectbackend.service.impl;

import com.inclusiveconnect.inclusiveconnectbackend.dto.response.AdminCompanyResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.AdminDashboardResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.AdminUserResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Company;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.enums.RoleName;
import com.inclusiveconnect.inclusiveconnectbackend.exception.UserNotFoundException;
import com.inclusiveconnect.inclusiveconnectbackend.repository.CompanyRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.JobRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.UserRepository;
import com.inclusiveconnect.inclusiveconnectbackend.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final com.inclusiveconnect.inclusiveconnectbackend.service.NotificationService notificationService;

    public AdminServiceImpl(UserRepository userRepository,
            CompanyRepository companyRepository,
            JobRepository jobRepository,
            com.inclusiveconnect.inclusiveconnectbackend.service.NotificationService notificationService) {
        this.userRepository = userRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.notificationService = notificationService;
    }

    @Override
    public AdminDashboardResponse getDashboard() {
        return AdminDashboardResponse.builder()
                .totalUsers(userRepository.count())
                .totalCandidates(userRepository.countByRole_Name(RoleName.ROLE_CANDIDATE))
                .totalEmployers(userRepository.countByRole_Name(RoleName.ROLE_EMPLOYER))
                .totalJobs(jobRepository.count())
                .pendingEmployerVerifications(companyRepository.countByVerifiedFalse())
                .build();
    }

    @Override
    @Transactional
    public List<AdminUserResponse> getAllUsers() {
        return userRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toUserResponse)
                .toList();
    }

    @Override
    @Transactional
    public AdminUserResponse setUserBlocked(Long userId, boolean blocked) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRole().getName() == RoleName.ROLE_ADMIN) {
            throw new IllegalArgumentException("Admin accounts cannot be blocked");
        }

        user.setActive(!blocked);
        return toUserResponse(userRepository.save(user));
    }

    @Override
    @Transactional
    public List<AdminCompanyResponse> getPendingEmployers() {
        return companyRepository.findByVerifiedFalse().stream()
                .map(this::toCompanyResponse)
                .toList();
    }

    @Override
    @Transactional
    public AdminCompanyResponse verifyEmployer(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));

        company.setVerified(true);
        AdminCompanyResponse response = toCompanyResponse(companyRepository.save(company));

        try {
            notificationService.createAndPush(
                    company.getUser().getId(),
                    "Company Verified",
                    "Company " + company.getCompanyName() + " has been verified.",
                    com.inclusiveconnect.inclusiveconnectbackend.entity.Notification.NotificationType.COMPANY_VERIFIED,
                    "/employer/profile");
        } catch (Exception e) {
            System.err.println("Failed to push company verification notification: " + e.getMessage());
        }

        return response;
    }

    private AdminUserResponse toUserResponse(User user) {
        return AdminUserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .role(user.getRole().getName().name())
                .isVerified(user.isVerified())
                .isActive(user.isActive())
                .build();
    }

    private AdminCompanyResponse toCompanyResponse(Company company) {
        return AdminCompanyResponse.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .industry(company.getIndustry())
                .verified(company.isVerified())
                .employerUserId(company.getUser().getId())
                .employerEmail(company.getUser().getEmail())
                .build();
    }
}