package com.docbrief.document.parser;

import com.docbrief.common.DocumentParsingException;
import com.docbrief.document.domain.DocumentType;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class DocxDocumentParser implements DocumentParser {

    @Override
    public DocumentType getSupportedType() { return DocumentType.DOCX; }

    @Override
    public ParsedText parse(InputStream inputStream) {
        ParsedText parsedText = new ParsedText();

        try(XWPFDocument document = new XWPFDocument(inputStream)) {
            int paragraphOrder = 1;
            List<XWPFParagraph> paragraphs = document.getParagraphs();

            for (XWPFParagraph xwpfParagraph : paragraphs) {
                String text = xwpfParagraph.getText();

                if (text == null || text.isBlank()) { continue; }

                // 원문 전체 텍스트 보존
                parsedText.appendFullText(text);

                ParsedParagraph paragraph = new ParsedParagraph(paragraphOrder++);

                int sentenceOrder = 1;
                String[] sentences = SentenceSplitter.splitToSentences(text);

                for (String sentence : sentences) {
                    if (sentence.isBlank()) continue;
                    paragraph.addSentence(new ParsedSentence(sentenceOrder++, sentence.trim()));
                }

                parsedText.addParagraph(paragraph);
            }
        }catch (IOException ioe){
            throw new DocumentParsingException();
        }

        return parsedText;
    }

}

