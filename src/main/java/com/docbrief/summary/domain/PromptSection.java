package com.docbrief.summary.domain;

public enum PromptSection {
    ROLE, // 역할 정의 (고정)
    INSTRUCTION, // 요약 목적
    CONSTRAINT, // 품질 통제용
    HIGHLIGHT_RULE, // 하이라이트 규칙
    OUTPUT_FORMAT, // 응답 형식
    VIOLATION_REASON // 규칙 위반 이유
}
