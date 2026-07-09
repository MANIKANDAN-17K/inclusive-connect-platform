package com.inclusiveconnect.inclusiveconnectbackend.service;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.CreateCompanyRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.UpdateCompanyRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.CompanyResponse;

public interface CompanyService {
    CompanyResponse createCompany(Long userId, CreateCompanyRequest request);

    CompanyResponse getMyCompany(Long userId);

    CompanyResponse getCompanyById(Long companyId);

    CompanyResponse updateCompany(Long userId, UpdateCompanyRequest request);

    com.inclusiveconnect.inclusiveconnectbackend.dto.response.CompanyLogoResponse uploadCompanyLogo(Long userId,
            org.springframework.web.multipart.MultipartFile file);

    com.inclusiveconnect.inclusiveconnectbackend.dto.response.CoverImageResponse uploadCompanyCover(Long userId,
            org.springframework.web.multipart.MultipartFile file);
}