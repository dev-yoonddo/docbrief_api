package com.docbrief.summary.ai;
import ch.qos.logback.core.net.server.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AnalysisTestConfig{

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
