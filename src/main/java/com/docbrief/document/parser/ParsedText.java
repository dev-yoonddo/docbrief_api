package com.docbrief.document.parser;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class ParsedText {
    // entity와의 분리 및 파싱 결과 저장을 위한 클래스(insert 전 DTO 역할)
    private final StringBuilder fullText;
    private final List<ParsedParagraph> paragraphs = new ArrayList<>();

    // 원문 보존용도
    public ParsedText(){
        this.fullText = new StringBuilder();
    }

    public void appendFullText(String line){
        fullText.append(line).append("\n");
    }

    // 구조화된 원문
    public void addParagraph(ParsedParagraph paragraph){
        this.paragraphs.add(paragraph);
    }

    public List<ParsedParagraph> getParagraphs(){
        return Collections.unmodifiableList(paragraphs);
    }

    public String getFullText(){
        return fullText.toString();
    }
}
