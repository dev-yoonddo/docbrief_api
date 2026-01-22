package com.docbrief.summary.service;

import com.docbrief.document.domain.DocumentType;
import com.docbrief.document.parser.DocumentParser;
import com.docbrief.document.parser.ParsedParagraph;
import com.docbrief.document.parser.ParsedSentence;
import com.docbrief.document.parser.ParsedText;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class UrlDocumentParser implements DocumentParser{
    public String extractText(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            // 스크립트, 스타일 제거
            doc.select("script, style, nav, footer, header, aside").remove();

            return doc.body().text();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ParsedText parseFromUrl(String url){
        ParsedText parsedText = new ParsedText();
        String urlText = this.extractText(url);
        String[] lines = urlText.split("\\R"); // 줄바꿈 기준
        int paragraphOrder = 1;
        List<String> paragraphLines = new ArrayList<>();

        for (String line : lines) {
            parsedText.appendFullText(line);

            if (line.isBlank()) {
                if (!paragraphLines.isEmpty()) {
                    parsedText.addParagraph(buildParagraph(paragraphOrder++, paragraphLines));
                    paragraphLines.clear();
                }
                continue;
            }
            paragraphLines.add(line);
        }

        if (!paragraphLines.isEmpty()) {
            parsedText.addParagraph(buildParagraph(paragraphOrder, paragraphLines));
        }

        return parsedText;
    }
    // 문단 기준 문장 구분
    private ParsedParagraph buildParagraph(int order, List<String> lines) {
        ParsedParagraph paragraph = new ParsedParagraph(order);

        int sentenceOrder = 1;
        for (String line : lines) {
            String[] sentences = splitToSentences(line);
            for (String sentence : sentences) {
                paragraph.addSentence(new ParsedSentence(sentenceOrder++, sentence.trim()));
            }
        }

        return paragraph;
    }

    private String[] splitToSentences(String text) {
        // 번호, 로마자, 괄호 번호
        if (text.matches("^(\\d+|[IVX]+|\\([0-9]+\\))\\.\\s+.*")) {
            return new String[]{text};
        }
        return text.split("(?<=[.!?])\\s+");
    }

    @Override
    public DocumentType getSupportedType() {
        return DocumentType.URL;
    }

    @Override
    public ParsedText parse(InputStream inputStream) throws IOException {
        return null;
    }
}
