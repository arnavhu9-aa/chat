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
public class SarvamAIService {

    @Value("${sarvam.api.key}")
    private String apiKey;

    @Value("${sarvam.model}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String askSarvam(List<ChatMessage> history, String userMessage) {

        String url = "https://api.sarvam.ai/v1/chat/completions";

        List<Map<String, String>> messages = new ArrayList<>();

        // ✅ system message (safe)
        messages.add(Map.of(
                "role", "system",
                "content", "You are a helpful AI assistant."
        ));

        // ✅ validate history safely
        if (history != null) {
            for (ChatMessage msg : history) {

                if (msg == null) continue;

                String role = msg.getRole();
                String content = msg.getContent();

                if (role == null || content == null) continue;

                messages.add(Map.of(
                        "role", role,
                        "content", content
                ));
            }
        }

        // ✅ validate user message
        if (userMessage == null || userMessage.isBlank()) {
            throw new IllegalArgumentException("userMessage cannot be null or empty");
        }

        messages.add(Map.of(
                "role", "user",
                "content", userMessage
        ));

        // ✅ request body
        Map<String, Object> body = new HashMap<>();
        body.put("model", model);
        body.put("messages", messages);
        body.put("temperature", 0.2);
        body.put("max_tokens", 1000);

        // ❗ safety check for model
        if (model == null || model.isBlank()) {
            throw new IllegalStateException("sarvam.model is missing in application.properties");
        }

        // ✅ headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        // ✅ API call
        ResponseEntity<Map> response =
                restTemplate.postForEntity(url, entity, Map.class);

        // ✅ extract response safely
        Map responseBody = response.getBody();
        if (responseBody == null) {
            return "Empty response from AI";
        }

        List choices = (List) responseBody.get("choices");
        if (choices == null || choices.isEmpty()) {
            return "No choices returned from AI";
        }

        Map firstChoice = (Map) choices.get(0);
        Map message = (Map) firstChoice.get("message");

        if (message == null || message.get("content") == null) {
            return "Invalid AI response format";
        }

        return message.get("content").toString();
    }
}