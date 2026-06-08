package com.docbrief.summary.domain;

public enum PromptSection {
    // 요약 관련
    ROLE, // 역할 정의 (고정)
    INSTRUCTION, // 요약 목적
    CONSTRAINT, // 품질 통제용
    HIGHLIGHT_RULE, // 하이라이트 규칙
    OUTPUT_FORMAT, // 응답 형식
    VIOLATION_REASON, // 규칙 위반 이유

    // 비교분석 관련
    COMPARE_ROLE,
    COMPARE_INSTRUCTION,
    COMPARE_CONSTRAINT,
    COMPARE_OUTPUT_FORMAT,
    COMPARE_VIOLATION_REASON
}
