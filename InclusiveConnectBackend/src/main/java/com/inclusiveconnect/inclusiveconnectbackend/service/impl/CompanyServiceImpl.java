package com.inclusiveconnect.inclusiveconnectbackend.service.impl;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.CreateCompanyRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.UpdateCompanyRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.CompanyResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Company;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.enums.RoleName;
import com.inclusiveconnect.inclusiveconnectbackend.exception.UnauthorizedException;
import com.inclusiveconnect.inclusiveconnectbackend.exception.UserNotFoundException;
import com.inclusiveconnect.inclusiveconnectbackend.repository.CompanyRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.UserRepository;
import com.inclusiveconnect.inclusiveconnectbackend.service.CompanyService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    public CompanyServiceImpl(CompanyRepository companyRepository, UserRepository userRepository) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public CompanyResponse createCompany(Long userId, CreateCompanyRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getRole().getName() != RoleName.ROLE_EMPLOYER) {
            throw new UnauthorizedException("Only employer accounts can create a company profile");
        }

        if (companyRepository.existsByUser_Id(userId)) {
            throw new IllegalArgumentException("You've already created a company profile");
        }

        Company company = Company.builder()
                .companyName(request.getCompanyName())
                .website(request.getWebsite())
                .industry(request.getIndustry())
                .description(request.getDescription())
                .companySize(request.getCompanySize())
                .verified(false)
                .user(user)
                .build();

        return toResponse(companyRepository.save(company));
    }

    @Override
    public CompanyResponse getMyCompany(Long userId) {
        Company company = companyRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("You haven't created a company profile yet"));
        return toResponse(company);
    }

    @Override
    public CompanyResponse getCompanyById(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new IllegalArgumentException("Company not found"));
        return toResponse(company);
    }

    @Override
    @Transactional
    public CompanyResponse updateCompany(Long userId, UpdateCompanyRequest request) {
        Company company = companyRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("You haven't created a company profile yet"));

        if (request.getCompanyName() != null) company.setCompanyName(request.getCompanyName());
        if (request.getWebsite() != null) company.setWebsite(request.getWebsite());
        if (request.getIndustry() != null) company.setIndustry(request.getIndustry());
        if (request.getDescription() != null) company.setDescription(request.getDescription());
        if (request.getCompanySize() != null) company.setCompanySize(request.getCompanySize());

        return toResponse(companyRepository.save(company));
    }

    private CompanyResponse toResponse(Company company) {
        return CompanyResponse.builder()
                .id(company.getId())
                .companyName(company.getCompanyName())
                .website(company.getWebsite())
                .industry(company.getIndustry())
                .description(company.getDescription())
                .companySize(company.getCompanySize())
                .logoUrl(company.getLogoUrl())
                .verified(company.isVerified())
                .employerUserId(company.getUser().getId())
                .build();
    }
}