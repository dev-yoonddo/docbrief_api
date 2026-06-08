package com.docbrief.summary.ai;

import com.docbrief.summary.domain.PromptSection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromptBuilder {
    private final PromptProvider promptProvider;

    /**
     * 요약용 프롬프트 생성
     *
     * @param documentText 문서 JSON 텍스트
     * @return 조립된 프롬프트 StringBuilder
     */
    public StringBuilder buildSummaryPrompt(String documentText) {
        StringBuilder sb = new StringBuilder();

        sb.append(promptProvider.get(PromptSection.ROLE)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.INSTRUCTION)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.CONSTRAINT)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.HIGHLIGHT_RULE)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.OUTPUT_FORMAT)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.VIOLATION_REASON)).append("\n\n");

        /*sb.append("문서 내용:\n");
        sb.append(documentText);*/

        return sb;
    }

    /**
     * 비교분석용 프롬프트 생성
     * 두 문서의 JSON을 받아 비교 분석 프롬프트를 조립한다.
     *
     * @param docAJson 문서 A의 JSON 텍스트
     * @param docBJson 문서 B의 JSON 텍스트
     * @return 조립된 비교 프롬프트 StringBuilder
     */
    public StringBuilder buildComparisonPrompt(String docAJson, String docBJson) {
        StringBuilder sb = new StringBuilder();

        sb.append(promptProvider.get(PromptSection.COMPARE_ROLE)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.COMPARE_INSTRUCTION)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.COMPARE_CONSTRAINT)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.COMPARE_OUTPUT_FORMAT)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.COMPARE_VIOLATION_REASON)).append("\n\n");

        sb.append("문서 A (비교 대상 1):\n").append(docAJson).append("\n\n");
        sb.append("문서 B (비교 대상 2):\n").append(docBJson);

        return sb;
    }
}
