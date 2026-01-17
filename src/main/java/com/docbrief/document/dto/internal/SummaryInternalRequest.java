package com.docbrief.document.dto.internal;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class SummaryInternalRequest {
    private Long documentId;
    private String fullText;
    private List<ParagraphDto> paragraphs;

    @Getter
    @AllArgsConstructor
    public static class ParagraphDto {
        private int order;
        private List<SentenceDto> sentences;
    }

    @Getter
    @AllArgsConstructor
    public static class SentenceDto {
        private int order;
        private String text;
    }

}
