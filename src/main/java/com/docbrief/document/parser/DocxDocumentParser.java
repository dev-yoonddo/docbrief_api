package com.docbrief.document.parser;

import com.docbrief.document.domain.DocumentType;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DocxDocumentParser implements DocumentParser {

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.DOCX;
    }

    @Override
    public String parse(File file) {
        // Poi 사용
        return "extracted docx to text";
    }
}

