package com.docbrief.summary.ai;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AnalysisApiTestRunner implements CommandLineRunner {

    private final AnalysisClient analysisClient;

    @Override
    public void run(String... args) {
        String result = analysisClient.summarize(
                "AI는 무엇인가 한 문장으로 설명해줘"
        );

        System.out.println("====== Gemini 응답 ======");
        System.out.println(result);
        System.out.println("========================");
    }
}
