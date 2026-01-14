package com.docbrief.document.parser;

import com.docbrief.document.domain.DocumentType;

import java.io.File;

public interface DocumentParser {
    boolean supports(DocumentType type);
    String parse(File file);
}
