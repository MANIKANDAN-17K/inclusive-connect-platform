package com.inclusiveconnect.inclusiveconnectbackend.service.impl;

import com.inclusiveconnect.inclusiveconnectbackend.dto.request.EducationRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.ExperienceRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.SkillRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.request.UpdateProfileRequest;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ProfileResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.*;
import com.inclusiveconnect.inclusiveconnectbackend.exception.UserNotFoundException;
import com.inclusiveconnect.inclusiveconnectbackend.repository.*;
import com.inclusiveconnect.inclusiveconnectbackend.service.ProfileService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.inclusiveconnect.inclusiveconnectbackend.service.FileStorageService;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ProfilePhotoResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ResumeResponse;
import com.inclusiveconnect.inclusiveconnectbackend.exception.InvalidFileTypeException;
import com.inclusiveconnect.inclusiveconnectbackend.exception.FileTooLargeException;
import com.inclusiveconnect.inclusiveconnectbackend.exception.FileStorageException;
import java.time.LocalDateTime;

import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

        private final UserRepository userRepository;
        private final ProfileRepository profileRepository;
        private final EducationRepository educationRepository;
        private final ExperienceRepository experienceRepository;
        private final SkillRepository skillRepository;
        private final FileStorageService fileStorageService;

        public ProfileServiceImpl(UserRepository userRepository,
                        ProfileRepository profileRepository,
                        EducationRepository educationRepository,
                        ExperienceRepository experienceRepository,
                        SkillRepository skillRepository,
                        FileStorageService fileStorageService) {
                this.userRepository = userRepository;
                this.profileRepository = profileRepository;
                this.educationRepository = educationRepository;
                this.experienceRepository = experienceRepository;
                this.skillRepository = skillRepository;
                this.fileStorageService = fileStorageService;
        }

        @Override
        @Transactional
        public ProfileResponse getMyProfile(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                Profile profile = profileRepository.findByUser_Id(userId).orElse(null);

                List<ProfileResponse.EducationDto> educations = educationRepository.findByUser_Id(userId).stream()
                                .map(this::toEducationDto)
                                .toList();

                List<ProfileResponse.ExperienceDto> experiences = experienceRepository.findByUser_Id(userId).stream()
                                .map(this::toExperienceDto)
                                .toList();

                List<ProfileResponse.SkillDto> skills = skillRepository.findByUser_Id(userId).stream()
                                .map(this::toSkillDto)
                                .toList();

                return ProfileResponse.builder()
                                .userId(user.getId())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .email(user.getEmail())
                                .headline(user.getHeadline())
                                .location(user.getLocation())
                                .profilePicture(user.getProfilePicture())
                                .about(profile != null ? profile.getAbout() : null)
                                .disabilityType(profile != null ? profile.getDisabilityType() : null)
                                .linkedinUrl(profile != null ? profile.getLinkedinUrl() : null)
                                .githubUrl(profile != null ? profile.getGithubUrl() : null)
                                .portfolioUrl(profile != null ? profile.getPortfolioUrl() : null)
                                .resumeUrl(user.getResumeUrl())
                                .educations(educations)
                                .experiences(experiences)
                                .skills(skills)
                                .build();
        }

        @Override
        @Transactional
        public ProfileResponse updateProfile(Long userId, UpdateProfileRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                user.setHeadline(request.getHeadline());
                user.setLocation(request.getLocation());
                userRepository.save(user);

                Profile profile = profileRepository.findByUser_Id(userId)
                                .orElseGet(() -> Profile.builder().user(user).build());

                profile.setAbout(request.getAbout());
                profile.setDisabilityType(request.getDisabilityType());
                profile.setLinkedinUrl(request.getLinkedinUrl());
                profile.setGithubUrl(request.getGithubUrl());
                profile.setPortfolioUrl(request.getPortfolioUrl());
                profileRepository.save(profile);

                return getMyProfile(userId);
        }

        @Override
        @Transactional
        public ProfileResponse.EducationDto addEducation(Long userId, EducationRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                Education education = Education.builder()
                                .institutionName(request.getInstitutionName())
                                .degree(request.getDegree())
                                .fieldOfStudy(request.getFieldOfStudy())
                                .startDate(request.getStartDate())
                                .endDate(request.getEndDate())
                                .cgpa(request.getCgpa())
                                .description(request.getDescription())
                                .user(user)
                                .build();

                return toEducationDto(educationRepository.save(education));
        }

        @Override
        @Transactional
        public ProfileResponse.EducationDto updateEducation(Long userId, Long educationId, EducationRequest request) {
                Education education = educationRepository.findByIdAndUser_Id(educationId, userId)
                                .orElseThrow(() -> new UserNotFoundException("Education record not found"));

                education.setInstitutionName(request.getInstitutionName());
                education.setDegree(request.getDegree());
                education.setFieldOfStudy(request.getFieldOfStudy());
                education.setStartDate(request.getStartDate());
                education.setEndDate(request.getEndDate());
                education.setCgpa(request.getCgpa());
                education.setDescription(request.getDescription());

                return toEducationDto(educationRepository.save(education));
        }

        @Override
        @Transactional
        public void deleteEducation(Long userId, Long educationId) {
                Education education = educationRepository.findByIdAndUser_Id(educationId, userId)
                                .orElseThrow(() -> new UserNotFoundException("Education record not found"));
                educationRepository.delete(education);
        }

        @Override
        @Transactional
        public ProfileResponse.ExperienceDto addExperience(Long userId, ExperienceRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                Experience experience = Experience.builder()
                                .companyName(request.getCompanyName())
                                .jobTitle(request.getJobTitle())
                                .employmentType(request.getEmploymentType())
                                .startDate(request.getStartDate())
                                .endDate(request.getEndDate())
                                .currentlyWorking(request.isCurrentlyWorking())
                                .description(request.getDescription())
                                .user(user)
                                .build();

                return toExperienceDto(experienceRepository.save(experience));
        }

        @Override
        @Transactional
        public ProfileResponse.ExperienceDto updateExperience(Long userId, Long experienceId,
                        ExperienceRequest request) {
                Experience experience = experienceRepository.findByIdAndUser_Id(experienceId, userId)
                                .orElseThrow(() -> new UserNotFoundException("Experience record not found"));

                experience.setCompanyName(request.getCompanyName());
                experience.setJobTitle(request.getJobTitle());
                experience.setEmploymentType(request.getEmploymentType());
                experience.setStartDate(request.getStartDate());
                experience.setEndDate(request.getEndDate());
                experience.setCurrentlyWorking(request.isCurrentlyWorking());
                experience.setDescription(request.getDescription());

                return toExperienceDto(experienceRepository.save(experience));
        }

        @Override
        @Transactional
        public void deleteExperience(Long userId, Long experienceId) {
                Experience experience = experienceRepository.findByIdAndUser_Id(experienceId, userId)
                                .orElseThrow(() -> new UserNotFoundException("Experience record not found"));
                experienceRepository.delete(experience);
        }

        @Override
        @Transactional
        public ProfileResponse.SkillDto addSkill(Long userId, SkillRequest request) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                if (skillRepository.existsByUser_IdAndSkillNameIgnoreCase(userId, request.getSkillName())) {
                        throw new IllegalArgumentException("You've already added this skill");
                }

                Skill.ProficiencyLevel level = parseProficiency(request.getProficiencyLevel());

                Skill skill = Skill.builder()
                                .skillName(request.getSkillName())
                                .proficiencyLevel(level)
                                .user(user)
                                .build();

                return toSkillDto(skillRepository.save(skill));
        }

        @Override
        @Transactional
        public void deleteSkill(Long userId, Long skillId) {
                Skill skill = skillRepository.findByIdAndUser_Id(skillId, userId)
                                .orElseThrow(() -> new UserNotFoundException("Skill not found"));
                skillRepository.delete(skill);
        }

        private Skill.ProficiencyLevel parseProficiency(String raw) {
                if (raw == null || raw.isBlank()) {
                        return Skill.ProficiencyLevel.BEGINNER;
                }
                try {
                        return Skill.ProficiencyLevel.valueOf(raw.toUpperCase());
                } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException(
                                        "Proficiency level must be one of: BEGINNER, INTERMEDIATE, ADVANCED, EXPERT");
                }
        }

        private ProfileResponse.EducationDto toEducationDto(Education e) {
                return ProfileResponse.EducationDto.builder()
                                .id(e.getId())
                                .institutionName(e.getInstitutionName())
                                .degree(e.getDegree())
                                .fieldOfStudy(e.getFieldOfStudy())
                                .startDate(e.getStartDate())
                                .endDate(e.getEndDate())
                                .cgpa(e.getCgpa())
                                .description(e.getDescription())
                                .build();
        }

        private ProfileResponse.ExperienceDto toExperienceDto(Experience e) {
                return ProfileResponse.ExperienceDto.builder()
                                .id(e.getId())
                                .companyName(e.getCompanyName())
                                .jobTitle(e.getJobTitle())
                                .employmentType(e.getEmploymentType())
                                .startDate(e.getStartDate())
                                .endDate(e.getEndDate())
                                .currentlyWorking(e.isCurrentlyWorking())
                                .description(e.getDescription())
                                .build();
        }

        private ProfileResponse.SkillDto toSkillDto(Skill s) {
                return ProfileResponse.SkillDto.builder()
                                .id(s.getId())
                                .skillName(s.getSkillName())
                                .proficiencyLevel(
                                                s.getProficiencyLevel() != null ? s.getProficiencyLevel().name() : null)
                                .build();
        }

        // MapStruct would normally auto-generate these to*Dto methods rather than
        // us hand-writing each mapping — we're keeping it manual through Sprint 2
        // since it's more transparent while you're still learning the mapping
        // logic itself. Worth revisiting with MapStruct once this pattern feels
        // repetitive across Jobs/Network/Chat later sprints.

        @Override
        @Transactional
        public ProfilePhotoResponse uploadProfilePhoto(Long userId,
                        org.springframework.web.multipart.MultipartFile file) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                if (file.isEmpty()) {
                        throw new IllegalArgumentException("Cannot upload empty file");
                }

                // Limit maximum 5 MB
                if (file.getSize() > 5 * 1024 * 1024) {
                        throw new FileTooLargeException("Profile picture must be less than 5 MB");
                }

                // Allowed formats
                String contentType = file.getContentType();
                if (contentType == null || (!contentType.equalsIgnoreCase("image/png") &&
                                !contentType.equalsIgnoreCase("image/jpeg") &&
                                !contentType.equalsIgnoreCase("image/jpg"))) {
                        throw new InvalidFileTypeException(
                                        "Unsupported file type. Only JPG, JPEG, and PNG are allowed");
                }

                try {
                        // Delete old asset from Cloudinary
                        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
                                fileStorageService.deleteFile(user.getProfilePicture());
                        }

                        String secureUrl = fileStorageService.uploadFile(file, "profile_photos");
                        user.setProfilePicture(secureUrl);
                        userRepository.save(user);

                        return new ProfilePhotoResponse(secureUrl);
                } catch (java.io.IOException e) {
                        throw new FileStorageException("Failed to upload profile picture: " + e.getMessage(), e);
                }
        }

        @Override
        @Transactional
        public ResumeResponse uploadResume(Long userId, org.springframework.web.multipart.MultipartFile file) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                if (file.isEmpty()) {
                        throw new IllegalArgumentException("Cannot upload empty file");
                }

                // Limit maximum 10 MB
                if (file.getSize() > 10 * 1024 * 1024) {
                        throw new FileTooLargeException("Resume must be less than 10 MB");
                }

                // Allowed format PDF
                String contentType = file.getContentType();
                if (contentType == null || !contentType.equalsIgnoreCase("application/pdf")) {
                        throw new InvalidFileTypeException("Unsupported file type. Only PDF is allowed");
                }

                try {
                        // Delete old resume from Cloudinary
                        if (user.getResumeUrl() != null && !user.getResumeUrl().isEmpty()) {
                                fileStorageService.deleteFile(user.getResumeUrl());
                        }

                        String secureUrl = fileStorageService.uploadFile(file, "resumes");
                        user.setResumeUrl(secureUrl);
                        user.setResumeFileName(file.getOriginalFilename());
                        user.setResumeUploadedAt(LocalDateTime.now());
                        userRepository.save(user);

                        return new ResumeResponse(secureUrl, user.getResumeFileName(), user.getResumeUploadedAt());
                } catch (java.io.IOException e) {
                        throw new FileStorageException("Failed to upload resume: " + e.getMessage(), e);
                }
        }

        @Override
        public ResumeResponse getResume(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                if (user.getResumeUrl() == null || user.getResumeUrl().isEmpty()) {
                        throw new IllegalArgumentException("No resume file found for this user");
                }

                return new ResumeResponse(user.getResumeUrl(), user.getResumeFileName(), user.getResumeUploadedAt());
        }

        @Override
        @Transactional
        public void deleteResume(Long userId) {
                User user = userRepository.findById(userId)
                                .orElseThrow(() -> new UserNotFoundException("User not found"));

                if (user.getResumeUrl() == null || user.getResumeUrl().isEmpty()) {
                        return;
                }

                try {
                        fileStorageService.deleteFile(user.getResumeUrl());
                        user.setResumeUrl(null);
                        user.setResumeFileName(null);
                        user.setResumeUploadedAt(null);
                        userRepository.save(user);
                } catch (java.io.IOException e) {
                        throw new FileStorageException("Failed to delete resume: " + e.getMessage(), e);
                }
        }
}