package com.docbrief.summary.service;

import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.document.service.SummaryRequestService;
import com.docbrief.summary.ai.AiClient;
import com.docbrief.summary.ai.PromptBuilder;
import com.docbrief.summary.domain.PromptStage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SummaryProcessor {
    private PromptBuilder promptBuilder;
    private AiClient aiClient;
    private ObjectMapper objectMapper;

    public String startSummaryEngine(SummaryInternalRequest summaryRequest){
        LocalDateTime date = null;
        String result = "";

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 첫번째 요청 시작
        System.out.println("Gemini Request ::: " + date.format(formatter));

        date = LocalDateTime.now();
        Instant start = Instant.now();
        String previewJson = this.summaryRquestToJson(summaryRequest);

        System.out.println(previewJson);

        StringBuilder prompt = new StringBuilder();
        prompt = promptBuilder.buildSummaryPrompt(previewJson.toString()); //documentText 원문 내용
        prompt.append("문서 내용:\n");
        prompt.append(previewJson);

        result = aiClient.summarize(
                prompt.toString()
        );

        // 첫번째 요청 끝
        date = LocalDateTime.now();
        Instant end = Instant.now();

        Duration duration = Duration.between(start, end);
        System.out.println("Gemini Response ::: " + date.format(formatter));
        System.out.println("소요시간 s ::: " + duration.toSeconds() + " / ms ::: " + duration.toMillis());

        System.out.println("Summary Result ================================================");
        System.out.println(result);
        System.out.println("===============================================================");

        return result;

    }
    public String summaryRquestToJson(SummaryInternalRequest request){
        String requestJson = "";
        try{
            requestJson =  objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new SummaryProcessingException("Failed to seriallize summary request", e);
        }
        return requestJson;
    }
}
