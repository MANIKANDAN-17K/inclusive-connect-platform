package com.inclusiveconnect.inclusiveconnectbackend.service;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.EducationRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.ExperienceRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.SkillRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.UpdateProfileRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ProfileResponse;

public interface ProfileService {

    ProfileResponse getMyProfile(Long userId);

    ProfileResponse updateProfile(Long userId, UpdateProfileRequest request);

    ProfileResponse.EducationDto addEducation(Long userId, EducationRequest request);

    ProfileResponse.EducationDto updateEducation(Long userId, Long educationId, EducationRequest request);

    void deleteEducation(Long userId, Long educationId);

    ProfileResponse.ExperienceDto addExperience(Long userId, ExperienceRequest request);

    ProfileResponse.ExperienceDto updateExperience(Long userId, Long experienceId, ExperienceRequest request);

    void deleteExperience(Long userId, Long experienceId);

    ProfileResponse.SkillDto addSkill(Long userId, SkillRequest request);

    void deleteSkill(Long userId, Long skillId);

    com.inclusiveconnect.inclusiveconnectbackend.dto.response.ProfilePhotoResponse uploadProfilePhoto(Long userId,
            org.springframework.web.multipart.MultipartFile file);

    com.inclusiveconnect.inclusiveconnectbackend.dto.response.ResumeResponse uploadResume(Long userId,
            org.springframework.web.multipart.MultipartFile file);

    com.inclusiveconnect.inclusiveconnectbackend.dto.response.ResumeResponse getResume(Long userId);

    void deleteResume(Long userId);
}