package com.ead.authuser.configs;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplateBean(RestTemplateBuilder builder) {
        // Additional Configuration for Custom Exception Handling can be placed here
        return builder.build();
    }
}
