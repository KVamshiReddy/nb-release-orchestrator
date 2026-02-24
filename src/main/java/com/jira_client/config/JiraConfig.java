package com.jira_client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class JiraConfig {

    @Value("${jira.base-url}")
    private String baseUrl;

    @Value("${jira.email}")
    private String email;

    @Value("${jira.api-token}")
    private String apiToken;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    public String getBaseUrl() { return baseUrl; }
    public String getEmail() { return email; }
    public String getApiToken() { return apiToken; }


}
