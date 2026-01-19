package com.docbrief.document.parser;

import com.docbrief.document.domain.DocumentType;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Component
public class DocxDocumentParser implements DocumentParser {

    @Override
    public DocumentType getSupportedType() { return DocumentType.DOCX; }

    @Override
    public ParsedText parse(InputStream inputStream) {

        // Poi 사용
        return new ParsedText();
    }
}

