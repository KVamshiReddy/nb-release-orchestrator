package com.jira_client.service;

import com.jira_client.config.JiraConfig;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


import java.util.Base64;

@Service
@AllArgsConstructor
public class JiraService {

    private static final Logger logger = LoggerFactory.getLogger(JiraService.class);
    private final RestTemplate restTemplate;
    private final JiraConfig jiraConfig;


    /*
    You can find the documentation for this at
    https://developer.atlassian.com/cloud/jira/platform/basic-auth-for-rest-apis/
     */

    private HttpHeaders createHeaders() {
        String credentials = jiraConfig.getEmail() + ":" + jiraConfig.getApiToken();
        String encoded = Base64.getEncoder().encodeToString(credentials.getBytes());
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic " + encoded);
        headers.set("Accept", "application/json");
        return headers;
    }

    public String getRawIssue(String key) {
        if (key.trim().isEmpty()) {
            throw new IllegalArgumentException("A valid key must be passed");
        }
        String url = jiraConfig.getBaseUrl()+"/rest/api/2/issue/"+key;
        logger.info("Fetching Jira Issue With The Key: {}", key);
        HttpEntity<String> entity = new HttpEntity<>(createHeaders());
        try{
            ResponseEntity<String> response = restTemplate.exchange(
                    url,           // the URL string
                    HttpMethod.GET, // the HTTP method
                    entity,        // HttpEntity wrapping your headers
                    String.class   // the response type you want back
            );
            logger.info("Successfully fetched issue: {}", key);
            return response.getBody();
        } catch (Exception e) {
            logger.error("Error fetching issue {}: {}", key, e.getMessage());
            throw  new RuntimeException("Issue Fetching Failed");
        }
    }

}
