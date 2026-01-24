package com.docbrief.summary.service;

import com.docbrief.common.ErrorCode;
import com.docbrief.common.SummaryProcessingException;
import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.document.repository.DocumentRepository;
import com.docbrief.summary.ai.GeminiClient;
import com.docbrief.summary.ai.OpenAiClient;
import com.docbrief.summary.ai.PromptBuilder;
import com.docbrief.summary.domain.SummaryJob;
import com.docbrief.summary.domain.SummaryResponse;
import com.docbrief.summary.domain.SummaryResult;
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


@Log4j2
@Service
@AllArgsConstructor
public class SummaryProcessor {
    private PromptBuilder promptBuilder;
    private GeminiClient geminiClient;
    private OpenAiClient openAiClient;
    private ObjectMapper objectMapper;
    private SummaryJobService summaryJobService;
    private SummaryResultService summaryResultService;
    private SummaryResultRepository summaryResultRepository;
    private DocumentRepository documentRepository;

    /**
     * SummaryInternalRequest 객체를 JSON 문자열로 직렬화 후
     * AI 요약 프롬프트에 전달한다.
     *
     * @param summaryRequest 요약 대상 문서의 구조화된 객체
     * @return JSON 형태의 문자열
     * @throws RuntimeException AI 요청 실패 시
     */
    public SummaryResponse startSummaryEngine(SummaryInternalRequest summaryRequest) {

        // 1. 문서 상태를 SUMMARIZING으로 업데이트
        int updated = documentRepository.updateStatusByDocumentId(
                summaryRequest.getDocumentId(),
                DocumentStatus.SUMMARIZING
        );

        // 이미 다른 요약이 진행중이면 예외 발생
        if (updated == 0) {
            throw new SummaryProcessingException(ErrorCode.SUMMARY_ALREADY_PROCESSING);
        }

        // 2. Job 생성
        SummaryJob summaryJob = summaryJobService.insertSummaryJob(summaryRequest.getDocumentId());

        String result;
        try {
            // 3. JSON 변환 & Prompt 생성
            String summaryJson = objectMapper.writeValueAsString(summaryRequest);
            StringBuilder prompt = promptBuilder.buildSummaryPrompt(summaryJson);
            prompt.append("문서 내용:\n").append(summaryJson);

            // 4. AI 요약
            summaryJobService.setJobProcessing(summaryJob.getJobId());
            result = this.callAiWithFallback(prompt.toString(), summaryJob);
            summaryJobService.setJobCompleted(summaryJob.getJobId());

            // 5. 문서 상태 완료 처리
            documentRepository.updateStatusByDocumentId(
                    summaryRequest.getDocumentId(),
                    DocumentStatus.SUMMARIZED);

            // 6. 결과 저장
            SummaryResult summaryResult = new SummaryResult();
            summaryResult.setJobId(summaryJob.getJobId());
            summaryResult.setContent(result);
            summaryResultService.insertSummaryResult(summaryResult);

            return getSummaryResult(summaryResult.getJobId());

        } catch (Exception e) {
            // 실패 시 document 상태 및 job 처리
            summaryJobService.setJobFailed(summaryJob.getJobId());
            documentRepository.updateStatusByDocumentId(
                    summaryRequest.getDocumentId(),
                    DocumentStatus.EXTRACTED); // EXTRACTED 상태로 롤백
            throw new SummaryProcessingException(ErrorCode.SUMMARY_AI_REQUEST_ERROR, e);
        }
    }

    /**
     * Gemini 먼저 호출하고, 429 TOO_MANY_REQUEST 발생 시 OpenAI로 fallback
     */
    private String callAiWithFallback(String prompt, SummaryJob summaryJob) {
        try {
            return geminiClient.summarize(prompt, summaryJob);
        } catch (SummaryProcessingException e) {
            if (isQuotaExceeded(e)) {
                log.warn("Gemini quota exceeded, fallback to OpenAI: {}", e.getMessage());
                // 재시도 시에는 Job 상태를 실패 처리하지 않고 그대로
                return openAiClient.summarize(prompt, summaryJob);
            }
            // 그 외 예외는 그대로 던짐
            throw e;
        }
    }

    private boolean isQuotaExceeded(SummaryProcessingException e) {
        return e.getStatusCode() == 429;
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
            throw new SummaryProcessingException(ErrorCode.SUMMARY_REQUEST_CONVERSION_ERROR, e);
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
            throw new SummaryProcessingException(ErrorCode.SUMMARY_RESPONSE_CONVERSION_ERROR, e);
        }
    }
}
