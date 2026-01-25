package com.docbrief.common;

import lombok.Getter;

@Getter
public enum ErrorMessage {

    //요약 관련 메시지
    SUMMARY_AI_REQUEST("AI 요약 요청에 실패했습니다."),
    SUMMARY_AI_RESPONSE("AI 요약 응답에 실패했습니다."),
    SUMMARY_AI_RATE_LIMIT("요약 요청이 많아 AI 호출 제한에 걸렸습니다."),
    SUMMARY_AI_TIMEOUT("AI 요약 타임아웃이 발생했습니다."),
    SUMMARY_CONVERSION_ERROR("요약 데이터 변환에 실패했습니다."),
    SUMMARY_ALREADY_PROCESSING("이미 요약이 진행 중인 문서입니다.");

    private final String message;

    ErrorMessage(String message) {
        this.message = message;
    }

}