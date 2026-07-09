package com.inclusiveconnect.inclusiveconnectbackend.controller;

import com.inclusiveconnect.inclusiveconnectbackend.common.ApiResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.EducationRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.ExperienceRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.SkillRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.UpdateProfileRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ProfileResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.service.ProfileService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Profile", description = "Profile, education, experience, and skills management")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/api/v1/profile/me")
    public ResponseEntity<ApiResponse<ProfileResponse>> getMyProfile(@AuthenticationPrincipal User currentUser) {
        ProfileResponse response = profileService.getMyProfile(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", response));
    }

    @PutMapping("/api/v1/profile")
    public ResponseEntity<ApiResponse<ProfileResponse>> updateProfile(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdateProfileRequest request) {
        ProfileResponse response = profileService.updateProfile(currentUser.getId(), request);
        return ResponseEntity.ok(ApiResponse.success("Profile updated successfully", response));
    }

    @PostMapping(value = "/api/v1/profile/photo", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<com.inclusiveconnect.inclusiveconnectbackend.dto.response.ProfilePhotoResponse>> uploadProfilePhoto(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        var response = profileService.uploadProfilePhoto(currentUser.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Profile photo uploaded successfully", response));
    }

    @PostMapping(value = "/api/v1/profile/resume", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<com.inclusiveconnect.inclusiveconnectbackend.dto.response.ResumeResponse>> uploadResume(
            @AuthenticationPrincipal User currentUser,
            @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        var response = profileService.uploadResume(currentUser.getId(), file);
        return ResponseEntity.ok(ApiResponse.success("Resume uploaded successfully", response));
    }

    @GetMapping("/api/v1/profile/resume")
    public ResponseEntity<ApiResponse<com.inclusiveconnect.inclusiveconnectbackend.dto.response.ResumeResponse>> getResume(
            @AuthenticationPrincipal User currentUser) {
        var response = profileService.getResume(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Resume metadata fetched successfully", response));
    }

    @DeleteMapping("/api/v1/profile/resume")
    public ResponseEntity<ApiResponse<Void>> deleteResume(
            @AuthenticationPrincipal User currentUser) {
        profileService.deleteResume(currentUser.getId());
        return ResponseEntity.ok(ApiResponse.success("Resume deleted successfully"));
    }

    // ---- Education ----

    @PostMapping("/api/v1/educations")
    public ResponseEntity<ApiResponse<ProfileResponse.EducationDto>> addEducation(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody EducationRequest request) {
        var dto = profileService.addEducation(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Education added", dto));
    }

    @PutMapping("/api/v1/educations/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse.EducationDto>> updateEducation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody EducationRequest request) {
        var dto = profileService.updateEducation(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Education updated", dto));
    }

    @DeleteMapping("/api/v1/educations/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEducation(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        profileService.deleteEducation(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Education deleted"));
    }

    // ---- Experience ----

    @PostMapping("/api/v1/experiences")
    public ResponseEntity<ApiResponse<ProfileResponse.ExperienceDto>> addExperience(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody ExperienceRequest request) {
        var dto = profileService.addExperience(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Experience added", dto));
    }

    @PutMapping("/api/v1/experiences/{id}")
    public ResponseEntity<ApiResponse<ProfileResponse.ExperienceDto>> updateExperience(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id,
            @Valid @RequestBody ExperienceRequest request) {
        var dto = profileService.updateExperience(currentUser.getId(), id, request);
        return ResponseEntity.ok(ApiResponse.success("Experience updated", dto));
    }

    @DeleteMapping("/api/v1/experiences/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExperience(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        profileService.deleteExperience(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Experience deleted"));
    }

    // ---- Skills ----

    @PostMapping("/api/v1/skills")
    public ResponseEntity<ApiResponse<ProfileResponse.SkillDto>> addSkill(
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody SkillRequest request) {
        var dto = profileService.addSkill(currentUser.getId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Skill added", dto));
    }

    @DeleteMapping("/api/v1/skills/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSkill(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Long id) {
        profileService.deleteSkill(currentUser.getId(), id);
        return ResponseEntity.ok(ApiResponse.success("Skill deleted"));
    }
}