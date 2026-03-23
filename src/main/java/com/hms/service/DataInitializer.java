package com.hms.service;

import com.hms.entity.Role;
import com.hms.entity.User;
import com.hms.enums.RoleType;
import com.hms.repository.RoleRepository;
import com.hms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Override
    public void run(String... args) {
        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
            .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_ADMIN).build()));
        Role receptionistRole = roleRepository.findByName(RoleType.ROLE_RECEPTIONIST)
            .orElseGet(() -> roleRepository.save(Role.builder().name(RoleType.ROLE_RECEPTIONIST).build()));

        if (!userRepository.existsByUsername("admin")) {
            User admin = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("admin123"))
                .fullName("System Administrator")
                .email("admin@hospital.local")
                .enabled(true)
                .role(adminRole)
                .build();
            userRepository.save(admin);
            auditLogService.log("SYSTEM", "USER_BOOTSTRAP", "Default admin user created (username: admin)");
        }

        if (!userRepository.existsByUsername("reception")) {
            User receptionist = User.builder()
                .username("reception")
                .password(passwordEncoder.encode("recept123"))
                .fullName("Front Desk Receptionist")
                .email("reception@hospital.local")
                .enabled(true)
                .role(receptionistRole)
                .build();
            userRepository.save(receptionist);
            auditLogService.log("SYSTEM", "USER_BOOTSTRAP", "Default receptionist user created (username: reception)");
        }
    }
}
