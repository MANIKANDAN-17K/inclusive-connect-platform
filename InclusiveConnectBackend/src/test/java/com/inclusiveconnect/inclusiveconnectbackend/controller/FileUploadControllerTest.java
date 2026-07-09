package com.inclusiveconnect.inclusiveconnectbackend.controller;

import com.inclusiveconnect.inclusiveconnectbackend.dto.response.CompanyLogoResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.CoverImageResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ProfilePhotoResponse;
import com.inclusiveconnect.inclusiveconnectbackend.dto.response.ResumeResponse;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Role;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.enums.RoleName;
import com.inclusiveconnect.inclusiveconnectbackend.service.CompanyService;
import com.inclusiveconnect.inclusiveconnectbackend.service.ProfileService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // bypass Spring Security filters for simple controller test
public class FileUploadControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfileService profileService;

    @MockBean
    private CompanyService companyService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_CANDIDATE);

        mockUser = User.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .role(role)
                .isActive(true)
                .build();

        // Inject simulated authentication
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(mockUser, null,
                mockUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void testUploadProfilePhotoSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "some image".getBytes());
        ProfilePhotoResponse response = new ProfilePhotoResponse("https://res.cloudinary.com/test/image.png");

        Mockito.when(profileService.uploadProfilePhoto(eq(1L), any())).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/profile/photo")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Profile photo uploaded successfully"))
                .andExpect(jsonPath("$.data.profilePictureUrl").value("https://res.cloudinary.com/test/image.png"));
    }

    @Test
    void testUploadResumeSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "resume.pdf", "application/pdf",
                "pdf content".getBytes());
        ResumeResponse response = new ResumeResponse("https://res.cloudinary.com/test/resume.pdf", "resume.pdf",
                LocalDateTime.now());

        Mockito.when(profileService.uploadResume(eq(1L), any())).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/profile/resume")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Resume uploaded successfully"))
                .andExpect(jsonPath("$.data.resumeUrl").value("https://res.cloudinary.com/test/resume.pdf"));
    }

    @Test
    void testGetResumeSuccess() throws Exception {
        ResumeResponse response = new ResumeResponse("https://res.cloudinary.com/test/resume.pdf", "resume.pdf",
                LocalDateTime.now());

        Mockito.when(profileService.getResume(eq(1L))).thenReturn(response);

        mockMvc.perform(get("/api/v1/profile/resume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.resumeUrl").value("https://res.cloudinary.com/test/resume.pdf"));
    }

    @Test
    void testDeleteResumeSuccess() throws Exception {
        Mockito.doNothing().when(profileService).deleteResume(eq(1L));

        mockMvc.perform(delete("/api/v1/profile/resume"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Resume deleted successfully"));
    }

    @Test
    void testUploadCompanyLogoSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "logo.jpg", "image/jpeg", "logo image".getBytes());
        CompanyLogoResponse response = new CompanyLogoResponse("https://res.cloudinary.com/test/logo.jpg");

        Mockito.when(companyService.uploadCompanyLogo(eq(1L), any())).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/company/logo")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.logoUrl").value("https://res.cloudinary.com/test/logo.jpg"));
    }

    @Test
    void testUploadCompanyCoverSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "cover.png", "image/png", "cover page".getBytes());
        CoverImageResponse response = new CoverImageResponse("https://res.cloudinary.com/test/cover.png");

        Mockito.when(companyService.uploadCompanyCover(eq(1L), any())).thenReturn(response);

        mockMvc.perform(multipart("/api/v1/company/cover")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.coverImageUrl").value("https://res.cloudinary.com/test/cover.png"));
    }
}
