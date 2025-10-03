package com.moodbeats.controller;

import com.moodbeats.dto.MoodAnalysisRequest;
import com.moodbeats.dto.MoodAnalysisResult;
import com.moodbeats.service.MoodAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mood")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*") // For development only
public class MoodBeatsController {

    private final MoodAnalysisService moodAnalysisService;

    @PostMapping("/analyze")
    public ResponseEntity<MoodAnalysisResult> analyzeMood(
            @Valid @RequestBody MoodAnalysisRequest request,
            Principal principal) {
        
        log.info("Received mood analysis request from user: {}", 
                principal != null ? principal.getName() : "anonymous");
        
        try {
            // For now, use a default username if no authentication
            String username = principal != null ? principal.getName() : "demo_user";
            
            MoodAnalysisResult result = moodAnalysisService.analyzeMood(username, request);
            
            log.info("Successfully analyzed mood with {} emotions and energy level {}", 
                    result.getPrimaryEmotions() != null ? result.getPrimaryEmotions().size() : 0,
                    result.getEnergyLevel());
            
            return ResponseEntity.ok(result);
            
        } catch (Exception e) {
            log.error("Error processing mood analysis request", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/demo")
    public ResponseEntity<Map<String, Object>> getDemoMoodAnalysis() {
        Map<String, Object> demo = new HashMap<>();
        demo.put("message", "Welcome to MoodBeats!");
        demo.put("description", "Translate your feelings into the perfect soundtrack");
        demo.put("example_moods", new String[]{
            "I'm feeling nostalgic about my college days but also excited about my future",
            "Stressed about work but trying to stay positive",
            "It's raining outside and I feel contemplative and cozy",
            "Just had an amazing workout and I'm energized",
            "Feeling lonely but hopeful that things will get better"
        });
        demo.put("features", new String[]{
            "AI-powered emotional analysis",
            "Personalized music recommendations",
            "Natural language mood descriptions",
            "Genre discovery based on feelings"
        });
        
        return ResponseEntity.ok(demo);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "MoodBeats API");
        health.put("version", "1.0.0");
        
        return ResponseEntity.ok(health);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleValidationError(IllegalArgumentException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Validation Error");
        error.put("message", e.getMessage());
        
        log.warn("Validation error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeError(RuntimeException e) {
        Map<String, String> error = new HashMap<>();
        error.put("error", "Processing Error");
        error.put("message", "An error occurred while processing your request");
        
        log.error("Runtime error in mood analysis", e);
        return ResponseEntity.internalServerError().body(error);
    }
}
