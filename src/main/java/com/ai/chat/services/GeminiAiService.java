package com.ai.chat.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ai.chat.models.ChatMessage;

@Service
public class GeminiAiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askGemini(List<ChatMessage> history, String userMessage) {
        // Gemini passes the API key as a query parameter in the URL
        String url = "https://generativelanguage.googleapis.com/v1beta/models/" + model + ":generateContent?key=" + apiKey;

        // Build Gemini contents list
        List<Map<String, Object>> contents = new ArrayList<>();

        // Previous chat history mapped to Gemini's format
        for (ChatMessage msg : history) {
            // Gemini uses 'model' instead of 'assistant'
            String role = msg.getRole().equalsIgnoreCase("assistant") ? "model" : msg.getRole();
           
            // Skip system messages here; system instructions have their own dedicated block in Gemini
            if (role.equalsIgnoreCase("system")) {
                continue;
            }

            contents.add(Map.of(
                    "role", role,
                    "parts", List.of(Map.of("text", msg.getContent()))
            ));
        }

        // Current user message
        contents.add(Map.of(
                "role", "user",
                "parts", List.of(Map.of("text", userMessage))
        ));

        // Request body
        Map<String, Object> body = new HashMap<>();
        body.put("contents", contents);

        // System instructions (Optional but included since your original code had one)
        Map<String, Object> systemInstruction = Map.of(
                "parts", List.of(Map.of("text", "You are a helpful AI assistant."))
        );
        body.put("systemInstruction", systemInstruction);

        // Generation configuration (Temperature, Max tokens)
        Map<String, Object> generationConfig = new HashMap<>();
        generationConfig.put("temperature", 0.2);
        generationConfig.put("maxOutputTokens", 1000); // Gemini uses maxOutputTokens
        body.put("generationConfig", generationConfig);

        // Headers (No Bearer token needed as key is in the URL)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            // API call
            ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);
            Map responseBody = response.getBody();

            System.out.println("========== GEMINI RESPONSE ==========");
            System.out.println(responseBody);
            System.out.println("=====================================");

            if (responseBody == null) {
                return "Error: Empty response from AI service.";
            }

            // Extract candidates (Gemini's equivalent of choices)
            Object candidatesObj = responseBody.get("candidates");
            if (candidatesObj == null) {
                return "Error: 'candidates' not found in AI response.";
            }

            List candidates = (List) candidatesObj;
            if (candidates.isEmpty()) {
                return "Error: AI returned empty candidates.";
            }

            // First candidate
            Map firstCandidate = (Map) candidates.get(0);

            // Extract content
            Map contentMap = (Map) firstCandidate.get("content");
            if (contentMap == null) {
                return "Error: 'content' not found in candidate.";
            }

            // Extract parts
            List partsList = (List) contentMap.get("parts");
            if (partsList == null || partsList.isEmpty()) {
                return "Error: 'parts' not found in content.";
            }

            // Extract text from the first part
            Map firstPart = (Map) partsList.get(0);
            Object textObj = firstPart.get("text");

            if (textObj != null) {
                return textObj.toString();
            }

            return "Error: Unable to parse AI response text.";

        }  catch (Exception e) {
            System.out.println("========== GEMINI ERROR ==========");
            e.printStackTrace();
            System.out.println("==================================");

            // Temporarily return the exact exception to debug it instantly on screen
            return "Error communicating with AI service: " + e.getMessage();
        }
    }
}