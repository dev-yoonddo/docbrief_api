package com.docbrief.comparison.service;

import com.docbrief.common.ErrorCode;
import com.docbrief.common.SummaryProcessingException;
import com.docbrief.comparison.domain.ComparisonJob;
import com.docbrief.comparison.domain.ComparisonResult;
import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.summary.ai.GeminiClient;
import com.docbrief.summary.ai.OpenAiClient;
import com.docbrief.summary.ai.PromptBuilder;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * 두 문서를 비교분석하는 처리기
 * - SummaryInternalRequest 형태의 두 문서를 JSON으로 변환 후 AI 비교분석
 * - Gemini Client를 우선적으로 사용하며, 할당량 초과 시 OpenAI로 fallback
 */
@Log4j2
@Service
@AllArgsConstructor
public class ComparisonProcessor {
    private PromptBuilder promptBuilder;
    private GeminiClient geminiClient;
    private OpenAiClient openAiClient;
    private ObjectMapper objectMapper;
    private ComparisonJobService comparisonJobService;
    private ComparisonResultService comparisonResultService;

    /**
     * 두 SummaryInternalRequest 객체를 받아 비교분석을 수행한다.
     *
     * @param comparisonJob 비교분석 작업
     * @param docARequest 비교 대상 문서 A
     * @param docBRequest 비교 대상 문서 B
     * @return ComparisonResult (비교분석 결과)
     * @throws SummaryProcessingException AI 요청 실패 또는 JSON 변환 실패 시
     */
    public ComparisonResult startComparisonEngine(
            ComparisonJob comparisonJob,
            SummaryInternalRequest docARequest,
            SummaryInternalRequest docBRequest) {

        String result;
        try {
            // 1. 두 문서를 JSON으로 변환
            String docAJson = objectMapper.writeValueAsString(docARequest);
            String docBJson = objectMapper.writeValueAsString(docBRequest);

            // 2. 비교분석 프롬프트 생성
            StringBuilder prompt = promptBuilder.buildComparisonPrompt(docAJson, docBJson);

            // 3. 작업 상태 업데이트: PROCESSING
            comparisonJobService.setJobProcessing(comparisonJob.getJobId());

            // 4. AI를 통한 비교분석 수행 (fallback 패턴 사용)
            result = this.callAiWithFallback(prompt.toString(), comparisonJob);

            // 5. 작업 상태 업데이트: COMPLETED
            comparisonJobService.setJobCompleted(comparisonJob.getJobId());

            // 6. 결과 저장
            ComparisonResult comparisonResult = new ComparisonResult(comparisonJob, result);
            comparisonResultService.insertComparisonResult(comparisonResult);

            return comparisonResult;

        } catch (JsonParseException e) {
            // JSON 파싱 구조적 오류 처리 (잘못된 JSON 형식)
            log.error("AI 응답 JSON 구조 오류: {}", e.getMessage(), e);
            comparisonJobService.setJobFailed(comparisonJob.getJobId());
            throw new SummaryProcessingException(ErrorCode.COMPARISON_PROCESSING_ERROR, e);

        } catch (JsonProcessingException e) {
            // JSON 처리 오류 (직렬화/역직렬화 실패)
            log.error("AI 응답 JSON 처리 실패: {}", e.getMessage(), e);
            comparisonJobService.setJobFailed(comparisonJob.getJobId());
            throw new SummaryProcessingException(ErrorCode.COMPARISON_PROCESSING_ERROR, e);

        } catch (HttpClientErrorException e) {
            // API 클라이언트 오류 처리 (400, 401, 403, 404 등)
            int statusCode = e.getStatusCode().value();
            if (statusCode == 429) {
                // 할당량 초과 (429 Too Many Requests)
                log.warn("API 할당량 초과 - 상태코드: {}", statusCode);
            } else {
                // 기타 클라이언트 오류
                log.error("API 클라이언트 오류 - 상태코드: {}, 메시지: {}", statusCode, e.getMessage());
            }
            comparisonJobService.setJobFailed(comparisonJob.getJobId());
            throw new SummaryProcessingException(ErrorCode.COMPARISON_PROCESSING_ERROR, e);

        } catch (Exception e) {
            // 예상치 못한 기타 오류
            log.error("비교분석 처리 중 예상치 못한 오류: {}", e.getMessage(), e);
            comparisonJobService.setJobFailed(comparisonJob.getJobId());
            throw new SummaryProcessingException(ErrorCode.COMPARISON_PROCESSING_ERROR, e);
        }
    }

    /**
     * Gemini를 먼저 호출하고, 할당량 초과(429) 발생 시 OpenAI로 fallback한다.
     *
     * @param prompt 비교분석 프롬프트
     * @param comparisonJob 비교분석 작업
     * @return AI의 비교분석 결과 (JSON 문자열)
     */
    private String callAiWithFallback(String prompt, ComparisonJob comparisonJob) {
        try {
            // Gemini를 이용한 비교분석
            return geminiClient.compare(prompt, comparisonJob);
        } catch (SummaryProcessingException e) {
            if (isQuotaExceeded(e)) {
                log.warn("Gemini quota exceeded for comparison, fallback to OpenAI: {}", e.getMessage());
                // OpenAI로 fallback
                return openAiClient.compare(prompt, comparisonJob);
            }
            // 그 외 예외는 그대로 던짐
            throw e;
        }
    }

    /**
     * 할당량 초과 여부 판단 (HTTP 429 상태 코드)
     *
     * @param e SummaryProcessingException
     * @return 할당량 초과 여부
     */
    private boolean isQuotaExceeded(SummaryProcessingException e) {
        return e.getStatusCode() == 429;
    }
}
