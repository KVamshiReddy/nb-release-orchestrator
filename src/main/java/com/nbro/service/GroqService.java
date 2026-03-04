package com.nbro.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class GroqService implements AIService {

    private static final Logger logger = LoggerFactory.getLogger(GroqService.class);

    @Value("${groq.api-key}")
    private String apiKey;

    @Value("${groq.base-url}")
    private String baseUrl;

    @Value("${groq.model}")
    private String model;

    private final RestTemplate restTemplate;
    @Override
    public String generateResponse(String prompt) {
        String url = baseUrl+"/openai/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.set("Accept", "application/json");
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("messages", List.of(message));
        requestBody.put("model", model);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        try{
            ResponseEntity<String> response = restTemplate.exchange(
                    url,           // the URL string
                    HttpMethod.POST, // the HTTP method
                    entity,        // HttpEntity wrapping your headers
                    String.class   // the response type you want back
            );
            logger.info("Successfully sent the prompt to Groq: {}", response);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode data = mapper.readTree(response.getBody());
            String output = data.get("choices").get(0).get("message").get("content").asText();
            return output;
        } catch (Exception e) {
            logger.error("Error sending the prompt to Groq: {}", e.getMessage());
            throw  new RuntimeException("Issue Fetching Failed");
        }
    }

}
