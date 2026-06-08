package com.docbrief.document.parser;

import com.docbrief.document.domain.DocumentType;
import com.docbrief.document.text.SentenceSplitter;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class PlainTextDocumentParser implements DocumentParser {

    @Override
    public DocumentType getSupportedType() { return DocumentType.PLAIN_TEXT; }

    @Override
    public ParsedText parse(InputStream inputStream) {
        throw new UnsupportedOperationException("PlainTextDocumentParser does not support InputStream parsing. Use parseFromText().");
    }

    @Override
    public ParsedText parseFromText(String text) {
        ParsedText parsedText = new ParsedText();

        String[] lines = text.split("\\r?\\n");
        int paragraphOrder = 1;

        for (String line : lines) {
            if (line == null || line.isBlank()) continue;

            parsedText.appendFullText(line);

            ParsedParagraph paragraph = new ParsedParagraph(paragraphOrder++);
            String[] sentences = SentenceSplitter.splitToSentences(line);

            int sentenceOrder = 1;
            for (String sentence : sentences) {
                if (sentence.isBlank()) continue;
                paragraph.addSentence(new ParsedSentence(sentenceOrder++, sentence.trim()));
            }

            parsedText.addParagraph(paragraph);
        }

        return parsedText;
    }
}
