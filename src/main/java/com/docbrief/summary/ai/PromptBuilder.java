package com.docbrief.summary.ai;

import com.docbrief.summary.domain.PromptSection;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromptBuilder {
    private final PromptProvider promptProvider;

    public String buildSummaryPrompt(String documentText) {
        StringBuilder sb = new StringBuilder();

        sb.append(promptProvider.get(PromptSection.ROLE)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.INSTRUCTION)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.CONSTRAINT)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.HIGHLIGHT_RULE)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.OUTPUT_FORMAT)).append("\n\n");
        sb.append(promptProvider.get(PromptSection.VIOLATION_REASON)).append("\n\n");

        sb.append("문서 내용:\n");
        sb.append(documentText);

        return sb.toString();
    }
}
