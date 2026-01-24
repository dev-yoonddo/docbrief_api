package com.docbrief.summary.ai;

import com.docbrief.common.ErrorCode;
import com.docbrief.common.SummaryProcessingException;
import com.docbrief.summary.ai.OpenAiResponse;
import com.docbrief.summary.domain.SummaryJob;
import com.docbrief.summary.domain.SummaryRequest;
import com.docbrief.summary.domain.SummaryResult;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Component
@RequiredArgsConstructor
public class OpenAiClient implements LlmClient {

    private final WebClient openAiWebClient;

    @Value("${openai.model.name}")
    private String model;

    @Override
    public String summarize(String text, SummaryJob summaryJob) {

        OpenAiRequest request = OpenAiRequest.fromText(model, text);

        OpenAiResponse response = openAiWebClient.post()
        .uri("/chat/completions")
        .bodyValue(request)
        .retrieve()
        .onStatus(
                status -> status.value() == 429,
                res -> Mono.error(new SummaryProcessingException(ErrorCode.SUMMARY_AI_RATE_LIMIT))
        )
        .bodyToMono(OpenAiResponse.class)
        .block();

        return Objects.requireNonNull(response).getText();
     }
}