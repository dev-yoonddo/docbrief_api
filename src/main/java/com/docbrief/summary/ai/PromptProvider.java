package com.docbrief.summary.ai;

import com.docbrief.summary.domain.PromptSection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromptProvider {

    private final PromptConfig promptConfig;
    private final ComparisonPromptConfig comparisonPromptConfig;

    public String get(PromptSection section) {
        return switch (section) {
            // 요약 프롬프트
            case ROLE -> promptConfig.getRole();
            case INSTRUCTION -> promptConfig.getInstruction();
            case CONSTRAINT -> promptConfig.getConstraint();
            case HIGHLIGHT_RULE -> promptConfig.getHighlightRule();
            case OUTPUT_FORMAT -> promptConfig.getOutputFormat();
            case VIOLATION_REASON -> promptConfig.getViolationReason();

            // 비교분석 프롬프트
            case COMPARE_ROLE -> comparisonPromptConfig.getRole();
            case COMPARE_INSTRUCTION -> comparisonPromptConfig.getInstruction();
            case COMPARE_CONSTRAINT -> comparisonPromptConfig.getConstraint();
            case COMPARE_OUTPUT_FORMAT -> comparisonPromptConfig.getOutputFormat();
            case COMPARE_VIOLATION_REASON -> comparisonPromptConfig.getViolationReasons();
        };
    }
}
