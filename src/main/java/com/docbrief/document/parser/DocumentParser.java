package com.docbrief.document.parser;

import com.docbrief.document.domain.DocumentType;

import java.io.IOException;
import java.io.InputStream;

public interface DocumentParser {
    DocumentType getSupportedType();
    ParsedText parse(InputStream inputStream) throws IOException;
    default ParsedText parseFromUrl(String url) throws IOException{
        throw new UnsupportedOperationException(
                "URL parsing is not supported for " + getSupportedType()
        );
    };
}
