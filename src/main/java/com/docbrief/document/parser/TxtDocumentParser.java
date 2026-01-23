package com.docbrief.document.parser;

import com.docbrief.common.DocumentParsingException;
import com.docbrief.document.domain.DocumentType;
import com.docbrief.document.text.SentenceSplitter;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
public class TxtDocumentParser implements DocumentParser{

    @Override
    public DocumentType getSupportedType() { return DocumentType.TXT; }

    @Override
    public ParsedText parse(InputStream inputStream){
        ParsedText parsedText = new ParsedText();

        try(BufferedReader reader
                    = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))){

            String line;
            int paragraphOrder = 1;
            List<String> paragraphLines = new ArrayList<>();

            while ((line = reader.readLine()) != null) {
                // 원문 전체 보존 용도
                parsedText.appendFullText(line);

                // 문단 구분
                if (line.isBlank()) {
                    if (!paragraphLines.isEmpty()) {
                        ParsedParagraph paragraph = buildParagraph(paragraphOrder++, paragraphLines);
                        parsedText.addParagraph(paragraph);
                        paragraphLines.clear();
                    }
                    continue;
                }

                paragraphLines.add(line);
            }

            // 마지막 문단
            if (!paragraphLines.isEmpty()) {
                ParsedParagraph paragraph = buildParagraph(paragraphOrder, paragraphLines);
                parsedText.addParagraph(paragraph);
            }

        }catch(IOException ioe){
            throw new DocumentParsingException();
        }

        return parsedText;
    }

    // 문단 기준 문장 구분
    private ParsedParagraph buildParagraph(int order, List<String> lines) {
        ParsedParagraph paragraph = new ParsedParagraph(order);

        int sentenceOrder = 1;
        for (String line : lines) {
            String[] sentences = SentenceSplitter.splitToSentences(line);
            for (String sentence : sentences) {
                paragraph.addSentence(new ParsedSentence(sentenceOrder++, sentence.trim()));
            }
        }

        return paragraph;
    }

}
