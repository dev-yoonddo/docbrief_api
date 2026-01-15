package com.docbrief.document.parser;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ParsedParagraph {
    private final int order;
    private final List<ParsedSentence> sentences = new ArrayList<>();

    public ParsedParagraph(int order){
        this.order = order;
    }

    public void addSentence(ParsedSentence sentence){
        this.sentences.add(sentence);
    }

    public List<ParsedSentence> getSentences(){
        return Collections.unmodifiableList(sentences);
    }

}
