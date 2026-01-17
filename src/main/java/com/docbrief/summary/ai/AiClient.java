package com.docbrief.summary.ai;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class AiClient {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public AiClient(
            RestTemplate restTemplate,
            @Value("${gemini.api.key}") String apiKey
    ) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    public String summarize(String text) {

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
                        + "?key=" + apiKey;

        AiRequest request = new AiRequest(
                List.of(
                        new AiRequest.Content(
                                List.of(new AiRequest.Part(text))
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AiRequest> entity =
                new HttpEntity<>(request, headers);

        ResponseEntity<AiResponse> response =
                restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        entity,
                        AiResponse.class
                );

        return response.getBody().getText();
    }
}
