package com.docbrief.summary.ai;

import com.docbrief.common.ErrorCode;
import com.docbrief.common.SummaryProcessingException;
import com.docbrief.summary.ai.OpenAiResponse;
import com.docbrief.summary.domain.SummaryJob;
import com.docbrief.summary.domain.SummaryRequest;
import com.docbrief.summary.domain.SummaryResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Objects;

@Log4j2
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
            clientResponse ->
                clientResponse.bodyToMono(String.class)
                    .flatMap(errorBody -> {
                        log.warn("OpenAI 429 Response Body: {}", errorBody);
                        return Mono.error(
                            new SummaryProcessingException(
                                    ErrorCode.SUMMARY_OPENAI_RATE_LIMIT, 429
                            )
                        );
                    })
        )
        .onStatus(
                HttpStatusCode::isError,
                clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("OpenAI Error Response: {}", errorBody);
                                    return Mono.error(
                                            new SummaryProcessingException(
                                                    ErrorCode.SUMMARY_OPENAI_REQUEST_ERROR
                                            )
                                    );
                                })
        )
        .bodyToMono(OpenAiResponse.class)
        .block();

        return Objects.requireNonNull(response).getText();
     }
}