package com.docbrief.summary.service;

import com.docbrief.common.SummaryProcessingException;
import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.document.service.SummaryRequestService;
import com.docbrief.summary.ai.AiClient;
import com.docbrief.summary.ai.PromptBuilder;
import com.docbrief.summary.domain.*;
import com.docbrief.summary.repository.SummaryJobRepository;
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
    private SummaryJobService summaryJobService;
    private SummaryResultService summaryResultService;

    public String startSummaryEngine(SummaryInternalRequest summaryRequest){
        LocalDateTime date = null;
        String result = "";

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");



        date = LocalDateTime.now();
        Instant start = Instant.now();
        // 첫번째 요청 시작
        System.out.println("Gemini Request ::: " + date.format(formatter));
        String previewJson = this.summaryRquestToJson(summaryRequest);
        System.out.println(previewJson);

        SummaryJob summaryJob = summaryJobService.insertSummaryJob(summaryRequest.getDocumentId());

        try {
            StringBuilder prompt = new StringBuilder();
            prompt = promptBuilder.buildSummaryPrompt(previewJson.toString()); //documentText 원문 내용
            prompt.append("문서 내용:\n");
            prompt.append(previewJson);

            System.out.println(summaryJob.getJobId() + " AIClient summarize() ============");
            summaryJobService.setJobProcessing(summaryJob.getJobId());

            result = aiClient.summarize(
                    prompt.toString()
                    , summaryJob
            );

            // 첫번째 요청 끝
            date = LocalDateTime.now();
            Instant end = Instant.now();;
            System.out.println(summaryJob.getJobId() + " AIClient summarize() ============");
            summaryJobService.setJobCompleted(summaryJob.getJobId());

            SummaryResult summaryResult = new SummaryResult();
            summaryResult.setJobId(summaryJob.getJobId());
            summaryResult.setContent(result);

            summaryResultService.insertSummaryResult(summaryResult);

            Duration duration = Duration.between(start, end);
            System.out.println("Gemini Response ::: " + date.format(formatter));
            System.out.println("소요시간 s ::: " + duration.toSeconds() + " / ms ::: " + duration.toMillis());

            System.out.println("Summary Result ================================================");
            System.out.println(result);
            System.out.println("===============================================================");
        }catch (Exception e){
            summaryJobService.setJobFailed(summaryJob.getJobId());
            throw new SummaryProcessingException(
                    ErrorCode.SUMMARY_AI_ERROR,
                    ErrorCode.SUMMARY_AI_ERROR.getMessage(),
                    e
            );
        }
        return result;

    }
    public String summaryRquestToJson(SummaryInternalRequest request){
        String requestJson = "";
        try{
            requestJson =  objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to seriallize summary request", e);
        }
        return requestJson;
    }
}
