package com.docbrief.document.parser;

import com.docbrief.document.domain.DocumentType;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class PdfDocumentParser implements DocumentParser {

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.PDF;
    }

    @Override
    public ParsedText parse(InputStream inputStream) {
        // PDFBox 사용
        return new ParsedText();
    }
}
