package com.docbrief.summary.ai;

import com.docbrief.summary.domain.PromptSection;
import com.docbrief.summary.domain.PromptStage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromptProvider {

    private final PromptConfig promptConfig;

    public String get(PromptStage stage, PromptSection section) {
        PromptConfig.Stage configStage = switch (stage) {
            case PREVIEW -> promptConfig.getPreview();
            case VALIDATE -> promptConfig.getValidate();
        };

        return switch (section) {
            case ROLE -> configStage.getRole();
            case INSTRUCTION -> configStage.getInstruction();
            case CONSTRAINT -> configStage.getConstraint();
            case HIGHLIGHT_RULE -> configStage.getHighlightRule();
            case OUTPUT_FORMAT -> configStage.getOutputFormat();
            case VIOLATION_REASON -> configStage.getViolationReason();
        };
    }
}
