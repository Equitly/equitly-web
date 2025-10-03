package com.moodbeats;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MoodBeatsApplication {

    public static void main(String[] args) {
        SpringApplication.run(MoodBeatsApplication.class, args);
    }
}
