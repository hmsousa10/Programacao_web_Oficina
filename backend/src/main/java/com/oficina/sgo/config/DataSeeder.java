package com.oficina.sgo.config;

import com.oficina.sgo.model.User;
import com.oficina.sgo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() == 0) {
            User admin = User.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .name("Administrador")
                    .email("admin@sgo.pt")
                    .role(User.Role.MANAGER)
                    .active(true)
                    .build();
            userRepository.save(admin);
            log.warn("SECURITY: Initial MANAGER user created with default credentials (admin/admin123). " +
                     "Change this password immediately in production!");
        }
    }
}
