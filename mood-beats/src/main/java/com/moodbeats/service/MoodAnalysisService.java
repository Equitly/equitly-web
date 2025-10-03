package com.moodbeats.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.moodbeats.dto.MoodAnalysisRequest;
import com.moodbeats.dto.MoodAnalysisResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MoodAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(MoodAnalysisService.class);

    private final ChatClient.Builder builder;
    private final ObjectMapper mapper;
    // private String debugMode = "false"; // might use this later
    
    public MoodAnalysisService(ChatClient.Builder chatClientBuilder, ObjectMapper objectMapper) {
        this.builder = chatClientBuilder;
        this.mapper = objectMapper;
    }

    @Value("${moodbeats.ai.mood-analysis.system-prompt}")
    private String systemPrompt;

    public MoodAnalysisResult analyzeMood(MoodAnalysisRequest request) {
        logger.info("starting analysis for: " + request.getMoodDescription());
        
        String moodText = request.getMoodDescription();
        if (moodText == null || moodText.trim().isEmpty()) {
            throw new IllegalArgumentException("mood description cant be empty");
        }

        try {
            // build the prompt
            String prompt = buildMoodAnalysisPrompt(request);
            // System.out.println("prompt: " + prompt); // for debugging

            ChatClient client = builder.build();
            String response = client.prompt()
                    .system(systemPrompt)
                    .user(prompt)
                    .call()
                    .content();

            logger.debug("got response: " + response.length() + " chars");

            // parse the response
            JsonNode analysis = parseAIResponse(response);
            MoodAnalysisResult result = convertToResult(request, analysis);
            
            return result;

        } catch (Exception ex) {
            logger.error("failed to analyze: " + ex.getMessage());
            throw new RuntimeException("analysis failed: " + ex.getMessage(), ex);
        }
    }

    private String buildMoodAnalysisPrompt(MoodAnalysisRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze this mood description and provide detailed emotional analysis:\n\n");
        prompt.append("Mood Description: \"").append(request.getMoodDescription()).append("\"\n");
        
        if (request.getContext() != null && !request.getContext().trim().isEmpty()) {
            prompt.append("Context: ").append(request.getContext()).append("\n");
        }
        
        prompt.append("\nProvide your analysis in this exact JSON format:\n");
        prompt.append("{\n");
        prompt.append("  \"primary_emotions\": [\"emotion1\", \"emotion2\"],\n");
        prompt.append("  \"energy_level\": 7,\n");
        prompt.append("  \"arousal_level\": 6,\n");
        prompt.append("  \"valence\": 8,\n");
        prompt.append("  \"music_characteristics\": {\n");
        prompt.append("    \"tempo_range\": \"medium-fast\",\n");
        prompt.append("    \"recommended_genres\": [\"indie-pop\", \"electronic\"],\n");
        prompt.append("    \"instrumentation\": \"upbeat, electronic elements\",\n");
        prompt.append("    \"mood_tags\": [\"uplifting\", \"energetic\"]\n");
        prompt.append("  },\n");
        prompt.append("  \"insight\": \"Brief explanation of the mood analysis\"\n");
        prompt.append("}\n\n");
        prompt.append("Remember:\n");
        prompt.append("- Energy level: 1 (very low energy) to 10 (very high energy)\n");
        prompt.append("- Arousal level: 1 (very calm) to 10 (very excited)\n");
        prompt.append("- Valence: 1 (very negative) to 10 (very positive)\n");
        prompt.append("- Use only the JSON format specified above\n");

        return prompt.toString();
    }

    private JsonNode parseAIResponse(String aiResponse) {
        try {
            String jsonStr = extractJsonFromResponse(aiResponse);
            return mapper.readTree(jsonStr);
        } catch (JsonProcessingException e) {
            logger.error("couldnt parse response: " + aiResponse);
            return createDefaultResponse();
        }
    }

    private String extractJsonFromResponse(String response) {
        // find json in response
        int start = response.indexOf('{');
        int end = response.lastIndexOf('}');
        
        if (start >= 0 && end >= 0 && end > start) {
            return response.substring(start, end + 1);
        }
        
        return response; // just return it if cant find json
    }

    private JsonNode createDefaultResponse() {
        try {
            String defaultJson = """
                {
                    "primary_emotions": ["neutral"],
                    "energy_level": 5,
                    "arousal_level": 5,
                    "valence": 5,
                    "music_characteristics": {
                        "tempo_range": "medium",
                        "recommended_genres": ["pop"],
                        "instrumentation": "balanced",
                        "mood_tags": ["neutral"]
                    },
                    "insight": "Unable to analyze mood - using default values"
                }
                """;
            return mapper.readTree(defaultJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to create default response", e);
        }
    }

    private MoodAnalysisResult convertToResult(MoodAnalysisRequest request, JsonNode aiAnalysis) {
        // Extract values from AI analysis
        Integer energyLevel = aiAnalysis.has("energy_level") ? aiAnalysis.get("energy_level").asInt() : null;
        Integer arousalLevel = aiAnalysis.has("arousal_level") ? aiAnalysis.get("arousal_level").asInt() : null;
        Integer valence = aiAnalysis.has("valence") ? aiAnalysis.get("valence").asInt() : null;
        
        String moodSummary = String.format("Energy: %s, Arousal: %s, Valence: %s", 
            energyLevel != null ? energyLevel : "?", 
            arousalLevel != null ? arousalLevel : "?", 
            valence != null ? valence : "?");

        MoodAnalysisResult.MoodAnalysisResultBuilder builder = MoodAnalysisResult.builder()
                .id(null) // No ID since we're not storing
                .moodDescription(request.getMoodDescription())
                .energyLevel(energyLevel)
                .arousalLevel(arousalLevel)
                .valence(valence)
                .moodSummary(moodSummary)
                .createdAt(LocalDateTime.now());

        // Extract primary emotions
        if (aiAnalysis.has("primary_emotions")) {
            List<String> emotions = new ArrayList<>();
            aiAnalysis.get("primary_emotions").forEach(emotion -> emotions.add(emotion.asText()));
            builder.primaryEmotions(emotions);
        }

        // Extract recommended genres from AI analysis
        if (aiAnalysis.has("music_characteristics") && 
            aiAnalysis.get("music_characteristics").has("recommended_genres")) {
            List<String> genres = new ArrayList<>();
            aiAnalysis.get("music_characteristics").get("recommended_genres")
                    .forEach(genre -> genres.add(genre.asText()));
            builder.recommendedGenres(genres);
        }

        // Extract insight
        if (aiAnalysis.has("insight")) {
            builder.analysisInsight(aiAnalysis.get("insight").asText());
        }

        // No song recommendations for now - just genre recommendations
        builder.recommendations(new ArrayList<>());

        logger.info("done analyzing: " + request.getMoodDescription().substring(0, Math.min(20, request.getMoodDescription().length())));
        return builder.build();
    }

    public List<String> extractEmotionsFromJsonNode(JsonNode emotionsNode) {
        List<String> emotions = new ArrayList<>();
        if (emotionsNode != null && emotionsNode.isArray()) {
            emotionsNode.forEach(emotion -> emotions.add(emotion.asText()));
        }
        return emotions;
    }

    public List<String> extractGenresFromAnalysis(JsonNode aiAnalysis) {
        List<String> genres = new ArrayList<>();
        if (aiAnalysis.has("music_characteristics") && 
            aiAnalysis.get("music_characteristics").has("recommended_genres")) {
            aiAnalysis.get("music_characteristics").get("recommended_genres")
                    .forEach(genre -> genres.add(genre.asText()));
        }
        return genres;
    }
}
