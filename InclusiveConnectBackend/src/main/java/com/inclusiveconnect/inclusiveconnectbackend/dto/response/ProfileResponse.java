package com.inclusiveconnect.inclusiveconnectbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ProfileResponse {
    private Long userId;
    private String firstName;
    private String lastName;
    private String email;
    private String headline;
    private String location;
    private String profilePicture;

    private String about;
    private String disabilityType;
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;
    private String resumeUrl;

    private List<EducationDto> educations;
    private List<ExperienceDto> experiences;
    private List<SkillDto> skills;

    @Getter
    @Builder
    @AllArgsConstructor
    public static class EducationDto {
        private Long id;
        private String institutionName;
        private String degree;
        private String fieldOfStudy;
        private LocalDate startDate;
        private LocalDate endDate;
        private Double cgpa;
        private String description;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class ExperienceDto {
        private Long id;
        private String companyName;
        private String jobTitle;
        private String employmentType;
        private LocalDate startDate;
        private LocalDate endDate;
        private boolean currentlyWorking;
        private String description;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class SkillDto {
        private Long id;
        private String skillName;
        private String proficiencyLevel;
    }
}