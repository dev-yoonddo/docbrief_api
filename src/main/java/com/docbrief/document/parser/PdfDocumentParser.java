package com.docbrief.document.parser;

import com.docbrief.document.domain.DocumentType;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class PdfDocumentParser implements DocumentParser {

    @Override
    public boolean supports(DocumentType type) {
        return type == DocumentType.PDF;
    }

    @Override
    public String parse(File file) {
        // PDFBox 사용
        return "extracted pdf to text";
    }
}
