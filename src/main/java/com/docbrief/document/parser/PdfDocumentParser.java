package com.docbrief.document.parser;

import com.docbrief.document.domain.DocumentType;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;

@Component
public class PdfDocumentParser implements DocumentParser {

    @Override
    public DocumentType getSupportedType() { return DocumentType.PDF; }

    @Override
    public ParsedText parse(InputStream inputStream) {
        ParsedText parsedText = new ParsedText();

        try (PDDocument document = PDDocument.load(inputStream)) {
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);

            int paragraphOrder = 1;
            for (String block : text.split("\\n\\n+")) {
                if (block.isBlank()) continue;

                // 원문 전체 텍스트 보존
                parsedText.appendFullText(block);

                ParsedParagraph paragraph = new ParsedParagraph(paragraphOrder++);
                int sentenceOrder = 1;

                for (String sentence : splitToSentences(block)) {
                    if (sentence.isBlank()) continue;
                    paragraph.addSentence(new ParsedSentence(sentenceOrder++, sentence.trim()));
                }

                parsedText.addParagraph(paragraph);
            }

        } catch (IOException e) {
            throw new RuntimeException("failed to parsing pdf file", e);
        }

        return parsedText;
    }

    private String[] splitToSentences(String text) {
        // 번호, 로마자, 괄호 번호
        if (text.matches("^(\\d+|[IVX]+|\\([0-9]+\\))\\.\\s+.*")) {
            return new String[]{text};
        }
        return text.split("(?<=[.!?])\\s+");
    }
}
