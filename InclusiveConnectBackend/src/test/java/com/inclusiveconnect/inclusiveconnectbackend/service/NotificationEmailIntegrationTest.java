package com.inclusiveconnect.inclusiveconnectbackend.service;

import com.inclusiveconnect.inclusiveconnectbackend.entity.Notification;
import com.inclusiveconnect.inclusiveconnectbackend.entity.Role;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.enums.RoleName;
import com.inclusiveconnect.inclusiveconnectbackend.repository.UserRepository;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class NotificationEmailIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @MockBean
    private JavaMailSender mailSender;

    private User testUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_CANDIDATE);

        // Fetch or create a test user
        testUser = userRepository.findByEmail("test_notify@example.com").orElseGet(() -> {
            User u = User.builder()
                    .firstName("Arthur")
                    .lastName("Dent")
                    .email("test_notify@example.com")
                    .password("pass123")
                    .role(role)
                    .isActive(true)
                    .isVerified(true)
                    .build();
            return userRepository.save(u);
        });

        // Mock mailSender MimeMessage creation
        MimeMessage mockMime = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mockMime);
    }

    @Test
    void testConnectionRequestEmailTrigger() throws Exception {
        // Trigger a Connection Request notification
        notificationService.createAndPush(
                testUser.getId(),
                "New connection request",
                "Ford Prefect wants to connect with you",
                Notification.NotificationType.CONNECTION_REQUEST,
                "/connections/pending");

        // Verify mailSender.send was called (timeout allows waiting for asynchronous
        // execution)
        verify(mailSender, timeout(3000).atLeastOnce()).send(any(MimeMessage.class));
    }

    @Test
    void testEmailFailureDoesNotRollbackTransaction() throws Exception {
        // Configure mock mailSender to throw exception to simulate SMTP failure
        doThrow(new org.springframework.mail.MailSendException("SMTP server temp down"))
                .when(mailSender).send(any(MimeMessage.class));

        // Triggering notification should swallow the SMTP error and complete
        // successfully
        assertDoesNotThrow(() -> {
            notificationService.createAndPush(
                    testUser.getId(),
                    "New connection request",
                    "Zaphod Beeblebrox wants to connect with you",
                    Notification.NotificationType.CONNECTION_REQUEST,
                    "/connections/pending");
        });
    }
}
