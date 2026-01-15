package com.docbrief.document.parser;

import com.docbrief.document.domain.DocumentType;

import java.io.IOException;
import java.io.InputStream;

public interface DocumentParser {
    boolean supports(DocumentType type);
    ParsedText parse(InputStream inputStream) throws IOException;
}
