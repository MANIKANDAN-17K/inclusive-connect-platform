package com.inclusiveconnect.inclusiveconnectbackend.controller;

import com.inclusiveconnect.inclusiveconnectbackend.common.ApiResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.CreateCompanyRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.UpdateCompanyRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.CompanyResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.service.CompanyService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Company", description = "Employer company profile management")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping("/api/v1/companies")
    public ResponseEntity<ApiResponse<CompanyResponse>> createCompany(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateCompanyRequest request) {
        CompanyResponse response = companyService.createCompany(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Company profile created", response));
    }

    @GetMapping("/api/v1/companies/me")
    public ResponseEntity<ApiResponse<CompanyResponse>> getMyCompany(@AuthenticationPrincipal User currentUser) {
        CompanyResponse response = companyService.getMyCompany(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Company fetched", response));
    }

    @GetMapping("/api/v1/companies/{id}")
    public ResponseEntity<ApiResponse<CompanyResponse>> getCompanyById(@PathVariable Long id) {
        CompanyResponse response = companyService.getCompanyById(id);
        return ResponseEntity.ok(ApiResponse.success("Company fetched", response));
    }

    @PutMapping("/api/v1/companies")
    public ResponseEntity<ApiResponse<CompanyResponse>> updateCompany(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdateCompanyRequest request) {
        CompanyResponse response = companyService.updateCompany(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Company updated", response));
    }

    @PostMapping(value = "/api/v1/company/logo", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<com.inclusiveconnect.inclusiveconnectbackend.dto.response.CompanyLogoResponse>> uploadCompanyLogo(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        var response = companyService.uploadCompanyLogo(currentUser.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Company logo uploaded successfully", response));
    }

    @PostMapping(value = "/api/v1/company/cover", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<com.inclusiveconnect.inclusiveconnectbackend.dto.response.CoverImageResponse>> uploadCompanyCover(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        var response = companyService.uploadCompanyCover(currentUser.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Company cover image uploaded successfully", response));
    }
}