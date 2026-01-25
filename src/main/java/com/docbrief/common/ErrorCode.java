package com.docbrief.common;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    /*** 공통 ***/
    INTERNAL_SERVER_ERROR(
            "COMMON-001",
            "서버 오류가 발생했습니다.",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),

    INVALID_REQUEST(
            "COMMON-002",
            "잘못된 요청입니다.",
            HttpStatus.BAD_REQUEST
    ),

    /*** 문서 ***/
    UNSUPPORTED_DOCUMENT_TYPE(
            "DOC-001",
            "지원하지 않는 문서형식입니다.",
            HttpStatus.BAD_REQUEST
    ),

    DOCUMENT_RESOURCE_REQUIRED(
            "DOC-002",
            "파일 업로드 또는 Url 입력이 필요합니다.",
            HttpStatus.BAD_REQUEST
    ),

    DOCUMENT_NOT_FOUND(
            "DOC-003",
            "문서를 찾을 수 없습니다.",
            HttpStatus.NOT_FOUND
    ),

    INVALID_DOCUMENT_STATUS(
            "DOC-004",
            "문서 상태가 올바르지 않습니다.",
            HttpStatus.CONFLICT
    ),

    DOC_PARSING_ERROR(
            "DOC-005",
            "문서 파싱 중 오류가 발생했습니다.",
            HttpStatus.INTERNAL_SERVER_ERROR
    ),

    /*** 요약 ***/
    // JSON 변환 관련
    SUMMARY_REQUEST_CONVERSION_ERROR(
            "SUMMARY_001",
            null,
            HttpStatus.INTERNAL_SERVER_ERROR
    ),
    SUMMARY_RESPONSE_CONVERSION_ERROR(
            "SUMMARY_002",
            null,
            HttpStatus.INTERNAL_SERVER_ERROR
    ),
    SUMMARY_ALREADY_PROCESSING(
        "SUMMARY_003",
        null,
        HttpStatus.CONFLICT
    ),

    // AI 호출 관련
    SUMMARY_AI_REQUEST_ERROR(
            "SUMMARY_004",
            null,
            HttpStatus.INTERNAL_SERVER_ERROR
    ),

    SUMMARY_AI_RESPONSE_ERROR(
            "SUMMARY_005",
            null,
            HttpStatus.INTERNAL_SERVER_ERROR
    ),
    SUMMARY_AI_TIMEOUT(
            "SUMMARY_006",
            null,
            HttpStatus.REQUEST_TIMEOUT
    ),
    SUMMARY_AI_RATE_LIMIT(
            "SUMMARY_007",
            null,
            HttpStatus.TOO_MANY_REQUESTS
    ),

    //OPENAI ERROR
    SUMMARY_OPENAI_REQUEST_ERROR(
            "SUMMARY_008",
            null,
            HttpStatus.INTERNAL_SERVER_ERROR
    ),
    SUMMARY_OPENAI_RESPONSE_ERROR(
            "SUMMARY_009",
            null,
            HttpStatus.INTERNAL_SERVER_ERROR
    ),
    SUMMARY_OPENAI_RATE_LIMIT(
            "SUMMARY_010",
            null,
            HttpStatus.TOO_MANY_REQUESTS
    ),

    //GEMINI ERROR
    SUMMARY_GEMINI_REQUEST_ERROR(
            "SUMMARY_011",
            null,
            HttpStatus.INTERNAL_SERVER_ERROR
    ),
    SUMMARY_GEMINI_RESPONSE_ERROR(
            "SUMMARY_012",
            null,
            HttpStatus.INTERNAL_SERVER_ERROR
    ),
    SUMMARY_GEMINI_RATE_LIMIT(
            "SUMMARY_013",
            null,
            HttpStatus.TOO_MANY_REQUESTS
    );

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }

    public String getCode() { return code; }
    public String getMessage() { return message; }
    public HttpStatus getStatus() { return status; }

}
