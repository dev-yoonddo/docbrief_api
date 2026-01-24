package com.docbrief.summary.ai;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OpenAiRequest {

    private String model;
    private List<Message> messages;

    @Getter
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
    }

    /**
     * GeminiRequest.fromText() 와 동일한 사용성
     */
    public static OpenAiRequest fromText(String model, String text) {
        return new OpenAiRequest(
                model,
                List.of(
                        new Message("system", "You are a helpful summarization AI."),
                        new Message("user", text)
                )
        );
    }
}