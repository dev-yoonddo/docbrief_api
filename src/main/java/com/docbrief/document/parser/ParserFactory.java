package com.docbrief.document.parser;

import com.docbrief.document.domain.DocumentType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ParserFactory {

    private final List<DocumentParser> parsers;

    public ParserFactory(List<DocumentParser> parsers) {
        this.parsers = parsers;
    }

    public DocumentParser getParser(DocumentType type) {
        return parsers.stream()
                .filter(p -> p.supports(type))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("지원하지 않는 타입"));
    }
}


