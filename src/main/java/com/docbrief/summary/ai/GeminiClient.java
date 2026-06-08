package com.docbrief.summary.ai;

import com.docbrief.common.ErrorCode;
import com.docbrief.common.SummaryProcessingException;
import com.docbrief.comparison.domain.ComparisonJob;
import com.docbrief.summary.domain.SummaryJob;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Component
public class GeminiClient implements LlmClient {

    private final RestTemplate restTemplate;
    private final String apiKey;

    public GeminiClient(
            RestTemplate restTemplate,
            @Value("${gemini.api.key}") String apiKey
    ) {
        this.restTemplate = restTemplate;
        this.apiKey = apiKey;
    }

    /**
     * Gemini API를 사용하여 문서를 요약한다.
     *
     * @param text 요약할 문서 텍스트 (JSON 형식)
     * @param summaryJob 요약 작업
     * @return 요약 결과 (JSON 문자열)
     */
    public String summarize(String text, SummaryJob summaryJob) {

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
                        + "?key=" + apiKey;

        GeminiRequest request = new GeminiRequest(
                List.of(
                        new GeminiRequest.Content(
                                List.of(new GeminiRequest.Part(text))
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GeminiRequest> entity =
                new HttpEntity<>(request, headers);

        try {
            ResponseEntity<GeminiResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, GeminiResponse.class);

            String result = Objects.requireNonNull(response.getBody()).getText();
            result = result.replaceAll("```json", "").replaceAll("```", "").trim();
            return result;

        } catch (HttpClientErrorException e) {
            // 429 처리
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new SummaryProcessingException(ErrorCode.SUMMARY_GEMINI_RATE_LIMIT, 429);
            }
            // 그 외 4xx
            throw new SummaryProcessingException(ErrorCode.SUMMARY_GEMINI_REQUEST_ERROR, e);
        } catch (HttpServerErrorException e) {
            // 5xx 서버 오류
            throw new SummaryProcessingException(ErrorCode.SUMMARY_GEMINI_RESPONSE_ERROR, e);
        } catch (Exception e) {
            // 기타 네트워크 오류 등
            throw new SummaryProcessingException(ErrorCode.SUMMARY_GEMINI_REQUEST_ERROR, e);
        }
    }

    /**
     * Gemini API를 사용하여 두 문서를 비교분석한다.
     *
     * @param text 비교분석할 문서 텍스트 (두 문서의 JSON 포함)
     * @param comparisonJob 비교분석 작업
     * @return 비교분석 결과 (JSON 문자열)
     */
    @Override
    public String compare(String text, ComparisonJob comparisonJob) {

        String url =
                "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
                        + "?key=" + apiKey;

        GeminiRequest request = new GeminiRequest(
                List.of(
                        new GeminiRequest.Content(
                                List.of(new GeminiRequest.Part(text))
                        )
                )
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GeminiRequest> entity =
                new HttpEntity<>(request, headers);

        try {
            ResponseEntity<GeminiResponse> response =
                    restTemplate.exchange(url, HttpMethod.POST, entity, GeminiResponse.class);

            String result = Objects.requireNonNull(response.getBody()).getText();
            result = result.replaceAll("```json", "").replaceAll("```", "").trim();
            return result;

        } catch (HttpClientErrorException e) {
            // 429 처리
            if (e.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                throw new SummaryProcessingException(ErrorCode.SUMMARY_GEMINI_RATE_LIMIT, 429);
            }
            // 그 외 4xx
            throw new SummaryProcessingException(ErrorCode.SUMMARY_GEMINI_REQUEST_ERROR, e);
        } catch (HttpServerErrorException e) {
            // 5xx 서버 오류
            throw new SummaryProcessingException(ErrorCode.SUMMARY_GEMINI_RESPONSE_ERROR, e);
        } catch (Exception e) {
            // 기타 네트워크 오류 등
            throw new SummaryProcessingException(ErrorCode.SUMMARY_GEMINI_REQUEST_ERROR, e);
        }
    }
}
