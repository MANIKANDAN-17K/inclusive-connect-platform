package com.inclusiveconnect.inclusiveconnectbackend.config;

import com.inclusiveconnect.inclusiveconnectbackend.entity.Role;
import com.inclusiveconnect.inclusiveconnectbackend.enums.RoleName;
import com.inclusiveconnect.inclusiveconnectbackend.repository.RoleRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;

    public DataSeeder(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
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
    }
}