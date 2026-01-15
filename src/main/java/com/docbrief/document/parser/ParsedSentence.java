package com.docbrief.document.parser;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ParsedSentence {
    private final int order;
    private final String text;
}
