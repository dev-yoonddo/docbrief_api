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
    )

    /*** 요약 ***/
    ;
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
