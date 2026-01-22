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

    /**
     * SummaryInternalRequest 객체를 JSON 문자열로 직렬화 후
     * AI 요약 프롬프트에 전달한다.
     *
     * @param summaryRequest 요약 대상 문서의 구조화된 객체
     * @return JSON 형태의 문자열
     * @throws RuntimeException AI 요청 실패 시
     */
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

    /**
     * SummaryInternalRequest 객체를 JSON 문자열로 직렬화 한다.
     *
     * @param summaryRequest 요약 대상 문서의 구조화된 객체
     * @return JSON 형태의 문자열
     * @throws RuntimeException JSON 변환 실패 시
     */
    public String summaryRequestToJson(SummaryInternalRequest summaryRequest){
        String requestJson = "";
        try{
            requestJson =  objectMapper.writeValueAsString(summaryRequest);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to seriallize summary request", e);
        }
        return requestJson;
    }

    /**
     * JSON 문자열을 SummaryResponse 객체로 역직렬화 한다.
     *
     * @param jobId 요약 결과 ID
     * @return SummaryResponse 객체
     * @throws RuntimeException JSON 변환 실패 시
     */
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
