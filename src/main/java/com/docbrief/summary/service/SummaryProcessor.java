package com.docbrief.summary.service;

import com.docbrief.document.dto.internal.SummaryInternalRequest;
import com.docbrief.summary.ai.AiClient;
import com.docbrief.summary.ai.PromptBuilder;
import com.docbrief.summary.domain.PromptStage;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class SummaryProcessor {
    private PromptBuilder promptBuilder;
    private AiClient aiClient;

    public void startSummaryEngine(SummaryInternalRequest summaryRequest){
        LocalDateTime date = null;
        StringBuilder prompt = new StringBuilder();
        String result = "";

        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // 첫번째 요청 시작
        date = LocalDateTime.now();
        Instant start = Instant.now();
        System.out.println("Gemini Request ::: " + date.format(formatter));

        List<SummaryInternalRequest.ParagraphDto> para = summaryRequest.getParagraphs();
        List<SummaryInternalRequest.SentenceDto> sentences = new ArrayList<>();
        for(SummaryInternalRequest.ParagraphDto s : para){
            sentences.addAll(s.getSentences());
        }

        System.out.println("sentences");
        System.out.println(sentences);

        System.out.println("첫번째 요청 PromptStage.PREVIEW");

        prompt = promptBuilder.buildSummaryPrompt(PromptStage.PREVIEW); //documentText 원문 내용

        prompt.append("문서 내용:\n");
        prompt.append(sentences);

        result = aiClient.summarize(
                prompt.toString()
        );

        // 첫번째 요청 끝
        date = LocalDateTime.now();
        Instant end = Instant.now();

        Duration duration = Duration.between(start, end);
        System.out.println("first Gemini Response ::: " + date.format(formatter));
        System.out.println("first 소요시간 s ::: " + duration.toSeconds() + " / ms ::: " + duration.toMillis());

        System.out.println("Summary Result ================================================");
        System.out.println(result);
        System.out.println("===============================================================");


    }
}
