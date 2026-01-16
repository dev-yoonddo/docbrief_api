package com.docbrief.summary.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class AnalysisRequest {

    private List<Content> contents;

    @Getter
    @AllArgsConstructor
    public static class Content {
        private List<Part> parts;
    }

    @Getter
    @AllArgsConstructor
    public static class Part {
        private String text;
    }

    // 🔹 편의용 팩토리 메서드 (추천)
    public static AnalysisRequest fromText(String text) {
        return new AnalysisRequest(
                List.of(
                        new Content(
                                List.of(new Part(text))
                        )
                )
        );
    }
}
