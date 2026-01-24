package com.docbrief.summary.ai;

import lombok.Getter;

import java.util.List;

@Getter
public class GeminiResponse {

    private List<Candidate> candidates;

    @Getter
    public static class Candidate {
        private Content content;
    }

    @Getter
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    public static class Part {
        private String text;
    }

    public String getText() {
        return candidates.get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();
    }
}
