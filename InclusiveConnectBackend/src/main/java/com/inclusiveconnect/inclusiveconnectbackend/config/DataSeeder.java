package com.inclusiveconnect.inclusiveconnectbackend.config;

import com.inclusiveconnect.inclusiveconnectbackend.entity.Role;
import com.inclusiveconnect.inclusiveconnectbackend.entity.User;
import com.inclusiveconnect.inclusiveconnectbackend.enums.RoleName;
import com.inclusiveconnect.inclusiveconnectbackend.repository.RoleRepository;
import com.inclusiveconnect.inclusiveconnectbackend.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        for (RoleName roleName : RoleName.values()) {
            roleRepository.findByName(roleName)
                    .orElseGet(() -> {
                        Role role = new Role();
                        role.setName(roleName);
                        return roleRepository.save(role);
                    });
        }

        seedAdminIfMissing();
    }

    private void seedAdminIfMissing() {
        String adminEmail = "admin@inclusiveconnect.com";

        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        Role adminRole = roleRepository.findByName(RoleName.ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException("ROLE_ADMIN not seeded"));

        User admin = User.builder()
                .firstName("Admin")
                .lastName("User")
                .email(adminEmail)
                .password(passwordEncoder.encode("Admin@12345")) // dev-only, change before deploying
                .role(adminRole)
                .isVerified(true)
                .isActive(true)
                .build();

        userRepository.save(admin);
    }
}