package com.docbrief.document.parser;

import com.docbrief.document.domain.DocumentType;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
public class ParserFactory {

    private final Map<DocumentType, DocumentParser> parserMap;

    public ParserFactory(List<DocumentParser> parsers) {
        this.parserMap = new EnumMap<>(DocumentType.class);
        for(DocumentParser parser : parsers){
            DocumentType type = parser.getSupportedType();
            parserMap.put(type, parser);
        }
    }

    public DocumentParser getParser(DocumentType type) {
        DocumentParser parser = parserMap.get(type);
        if(parser == null){
            throw new IllegalArgumentException("no parser registered for type ::: type : " + type);
        }
        return parser;
   }
}


