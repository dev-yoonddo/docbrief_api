package com.docbrief.summary.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class SummaryResponse {

    private String summaryText;
    private List<Highlight> highlights;
    private String violationReason;


    @Getter
    public static class Highlight {
        private String type; // KEYWORD / SENTENCE
        private String value;
        private Source source;
    }

    @Getter
    public static class Source {
        private int paragraphOrder;
        private int sentenceOrder;
    }

}
