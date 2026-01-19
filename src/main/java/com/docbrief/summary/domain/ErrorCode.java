package com.docbrief.summary.domain;

import lombok.Getter;

@Getter
public enum ErrorCode {
    // JSON 변환 관련
    SUMMARY_JSON_ERROR("SUMMARY_001", "Summary 요청 JSON 변환 실패"),

    // AI 호출 관련
    SUMMARY_AI_ERROR("SUMMARY_002", "AI 요약 요청 실패"),

    // 입력값 검증 실패
    INVALID_REQUEST("REQUEST_001", "요청 데이터가 유효하지 않음"),

    // 그 외 일반 오류
    UNKNOWN_ERROR("GENERIC_999", "알 수 없는 오류 발생");

    private final String code;    // 오류 식별 코드
    private final String message; // 기본 오류 메시지

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

}