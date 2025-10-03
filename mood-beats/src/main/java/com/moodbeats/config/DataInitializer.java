package com.moodbeats.config;

import com.moodbeats.entity.User;
import com.moodbeats.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Initializing sample data...");
        
        // Create demo user if it doesn't exist
        if (!userRepository.existsByUsername("demo_user")) {
            User demoUser = new User();
            demoUser.setUsername("demo_user");
            demoUser.setEmail("demo@moodbeats.com");
            demoUser.setPasswordHash("$2a$10$example_hash"); // In real app, properly hash this
            demoUser.setFirstName("Demo");
            demoUser.setLastName("User");
            demoUser.setIsActive(true);
            
            userRepository.save(demoUser);
            log.info("Created demo user: {}", demoUser.getUsername());
        }

        log.info("Data initialization completed. Users in database: {}", userRepository.count());
    }
}
