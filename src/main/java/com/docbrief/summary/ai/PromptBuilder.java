package com.docbrief.summary.ai;

import com.docbrief.summary.domain.PromptSection;
import com.docbrief.summary.domain.PromptStage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PromptBuilder {
    private final PromptProvider promptProvider;

    public StringBuilder buildSummaryPrompt(PromptStage stage) {
        StringBuilder sb = new StringBuilder();

        sb.append(promptProvider.get(stage, PromptSection.ROLE)).append("\n\n");
        sb.append(promptProvider.get(stage, PromptSection.INSTRUCTION)).append("\n\n");
        sb.append(promptProvider.get(stage, PromptSection.CONSTRAINT)).append("\n\n");
        sb.append(promptProvider.get(stage, PromptSection.HIGHLIGHT_RULE)).append("\n\n");
        sb.append(promptProvider.get(stage, PromptSection.OUTPUT_FORMAT)).append("\n\n");
        sb.append(promptProvider.get(stage, PromptSection.VIOLATION_REASON)).append("\n\n");

        /*sb.append("문서 내용:\n");
        sb.append(documentText);*/

        return sb;
    }
}
