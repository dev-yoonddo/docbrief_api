package com.docbrief.summary.ai;

import com.docbrief.summary.domain.PromptSection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromptProvider {

    private final PromptConfig promptConfig;

    public String get(PromptSection section) {
        return switch (section) {
            case ROLE -> promptConfig.getRole();
            case INSTRUCTION -> promptConfig.getInstruction();
            case CONSTRAINT -> promptConfig.getConstraint();
            case HIGHLIGHT_RULE -> promptConfig.getHighlightRule();
            case OUTPUT_FORMAT -> promptConfig.getOutputFormat();
            case VIOLATION_REASON -> promptConfig.getViolationReason();
        };
    }
}
