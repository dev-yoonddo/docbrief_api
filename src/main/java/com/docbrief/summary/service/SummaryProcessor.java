package com.docbrief.summary.service;

import com.docbrief.common.SummaryProcessingException;
import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.document.service.SummaryRequestService;
import com.docbrief.summary.ai.AiClient;
import com.docbrief.summary.ai.AiResponse;
import com.docbrief.summary.ai.PromptBuilder;
import com.docbrief.summary.domain.*;
import com.docbrief.summary.repository.SummaryJobRepository;
import com.docbrief.summary.repository.SummaryResultRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Log4j2
@Service
@AllArgsConstructor
public class SummaryProcessor {
    private PromptBuilder promptBuilder;
    private AiClient aiClient;
    private ObjectMapper objectMapper;
    private SummaryJobService summaryJobService;
    private SummaryResultService summaryResultService;
    private SummaryResultRepository summaryResultRepository;

    public String startSummaryEngine(SummaryInternalRequest summaryRequest){
        LocalDateTime date = null;
        String result = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        date = LocalDateTime.now();
        Instant start = Instant.now();

        // 첫번째 요청 시작
        log.info("Gemini Request ::: {}", date.format(formatter));
        SummaryJob summaryJob = summaryJobService.insertSummaryJob(summaryRequest.getDocumentId());

        try {
            // JSON 파싱
            String summaryJson = this.summaryRequestToJson(summaryRequest);

            // Prompt 생성
            StringBuilder prompt = new StringBuilder();
            prompt = promptBuilder.buildSummaryPrompt(summaryJson.toString());
            prompt.append("문서 내용:\n");
            prompt.append(summaryJson);

            // AI 요청 시작
            log.info("START AIClient summarize ::: {}", summaryJob.getJobId());

            // STATUS 값 저장
            summaryJobService.setJobProcessing(summaryJob.getJobId());

            // AI 요청
            result = aiClient.summarize(
                    prompt.toString()
                    , summaryJob
            );

            // AI 요청 끝
            log.info("END AIClient summarize ::: {}", summaryJob.getJobId());

            date = LocalDateTime.now();
            Instant end = Instant.now();;

            // STATUS 값 저장
            summaryJobService.setJobCompleted(summaryJob.getJobId());
            // 요약 결과 저장
            SummaryResult summaryResult = new SummaryResult();
            summaryResult.setJobId(summaryJob.getJobId());
            summaryResult.setContent(result);

            summaryResultService.insertSummaryResult(summaryResult);

            Duration duration = Duration.between(start, end);
            log.info("Gemini Response ::: {}", date.format(formatter));
            log.info("소요시간 s ::: {} / ms ::: {}", duration.toSeconds(), duration.toMillis());
            log.info("Summary Result ================================================");
            log.info(result);
            log.info("===============================================================");

            SummaryResponse response = this.getSummaryResult(summaryResult.getJobId());
            log.info("AI결과 재파싱 !");
            log.info(response);
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

    /*
     * @param : SummaryInternalRequest
     * Document -> JSON 파싱
     * */
    public String summaryRequestToJson(SummaryInternalRequest request){
        String requestJson = "";
        try{
            requestJson =  objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to seriallize summary request", e);
        }
        return requestJson;
    }
    /*
    * @param : Long
    * Summary -> JSON 파싱
    * */
    public SummaryResponse getSummaryResult(Long jobId) {
        try {
        SummaryResult result = summaryResultRepository.findByJobId(jobId)
                .orElseThrow(() -> new IllegalArgumentException("요약 결과 없음"));

            return objectMapper.readValue(result.getContent(), SummaryResponse.class);
        } catch (JsonProcessingException e) {
            throw new SummaryProcessingException(
                    ErrorCode.SUMMARY_JSON_ERROR,
                    "요약 결과 JSON 파싱 실패",
                    e
            );
        }
    }
}
