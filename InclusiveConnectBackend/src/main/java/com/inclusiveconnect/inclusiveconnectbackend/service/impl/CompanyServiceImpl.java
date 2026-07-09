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
import com.inclusiveconnect.inclusiveconnectbackend.service.FileStorageService;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.CompanyLogoResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.CoverImageResponse;
import com.inclusiveconnect.inclusiveconnectbackend.exception.InvalidFileTypeException;
import com.inclusiveconnect.inclusiveconnectbackend.exception.FileTooLargeException;
import com.inclusiveconnect.inclusiveconnectbackend.exception.FileStorageException;

@Service
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public CompanyServiceImpl(CompanyRepository companyRepository, UserRepository userRepository,
            FileStorageService fileStorageService) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
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

        if (request.getCompanyName() != null)
            company.setCompanyName(request.getCompanyName());
        if (request.getWebsite() != null)
            company.setWebsite(request.getWebsite());
        if (request.getIndustry() != null)
            company.setIndustry(request.getIndustry());
        if (request.getDescription() != null)
            company.setDescription(request.getDescription());
        if (request.getCompanySize() != null)
            company.setCompanySize(request.getCompanySize());

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
                .coverImageUrl(company.getCoverImageUrl())
                .verified(company.isVerified())
                .employerUserId(company.getUser().getId())
                .build();
    }

    @Override
    @Transactional
    public CompanyLogoResponse uploadCompanyLogo(Long userId, org.springframework.web.multipart.MultipartFile file) {
        Company company = companyRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("You haven't created a company profile yet"));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new FileTooLargeException("Company logo must be less than 5 MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equalsIgnoreCase("image/png") &&
                !contentType.equalsIgnoreCase("image/jpeg") &&
                !contentType.equalsIgnoreCase("image/jpg") &&
                !contentType.equalsIgnoreCase("image/svg+xml"))) {
            throw new InvalidFileTypeException("Unsupported file type. Only PNG, JPEG, and SVG are allowed");
        }

        try {
            if (company.getLogoUrl() != null && !company.getLogoUrl().isEmpty()) {
                fileStorageService.deleteFile(company.getLogoUrl());
            }

            String secureUrl = fileStorageService.uploadFile(file, "company_logos");
            company.setLogoUrl(secureUrl);
            companyRepository.save(company);

            return new CompanyLogoResponse(secureUrl);
        } catch (java.io.IOException e) {
            throw new FileStorageException("Failed to upload company logo: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public CoverImageResponse uploadCompanyCover(Long userId, org.springframework.web.multipart.MultipartFile file) {
        Company company = companyRepository.findByUser_Id(userId)
                .orElseThrow(() -> new IllegalArgumentException("You haven't created a company profile yet"));

        if (file.isEmpty()) {
            throw new IllegalArgumentException("Cannot upload empty file");
        }

        if (file.getSize() > 10 * 1024 * 1024) {
            throw new FileTooLargeException("Cover image must be less than 10 MB");
        }

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equalsIgnoreCase("image/png") &&
                !contentType.equalsIgnoreCase("image/jpeg") &&
                !contentType.equalsIgnoreCase("image/jpg"))) {
            throw new InvalidFileTypeException("Unsupported file type. Only JPG, JPEG, and PNG are allowed");
        }

        try {
            if (company.getCoverImageUrl() != null && !company.getCoverImageUrl().isEmpty()) {
                fileStorageService.deleteFile(company.getCoverImageUrl());
            }

            String secureUrl = fileStorageService.uploadFile(file, "company_covers");
            company.setCoverImageUrl(secureUrl);
            companyRepository.save(company);

            return new CoverImageResponse(secureUrl);
        } catch (java.io.IOException e) {
            throw new FileStorageException("Failed to upload company cover image: " + e.getMessage(), e);
        }
    }
}