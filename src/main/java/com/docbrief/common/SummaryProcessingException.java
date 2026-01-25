package com.docbrief.common;

import com.docbrief.document.domain.DocumentStatus;
import lombok.Getter;

import java.util.Map;
import java.util.Set;

@Getter
public class SummaryProcessingException extends BusinessException {

    private final int statusCode;
    private final String errorMessage;

    //public 메서드는 입력값 정규화 역할 :: 모두 private으로 위임
    public SummaryProcessingException(ErrorCode errorCode) {
        this(errorCode, null, errorCode.getStatus().value()
        );
    }

    public SummaryProcessingException(ErrorCode errorCode, int statusCode) {
        this(errorCode, null, statusCode);
    }

    public SummaryProcessingException(ErrorCode errorCode, Throwable cause) {
        this(errorCode, cause, errorCode.getStatus().value());
    }

    private SummaryProcessingException(ErrorCode errorCode, Throwable cause, int statusCode) {
        super(errorCode,
                resolveMessage(errorCode) != null
                ? resolveMessage(errorCode)
                : errorCode.getMessage()
        );

        this.statusCode = statusCode;
        this.errorMessage = getMessage();

        if (cause != null) {
            initCause(cause);
        }
    }

    //그룹화 불필요한 ERROR_CODE
    private static final Map<ErrorCode, ErrorMessage> EXPLICIT_MAP = Map.of(
            ErrorCode.SUMMARY_ALREADY_PROCESSING, ErrorMessage.SUMMARY_ALREADY_PROCESSING
    );

    //RATE_LIMIT 계열 ERROR_CODE
    private static final Set<ErrorCode> RATE_LIMIT_CODES = Set.of(
            ErrorCode.SUMMARY_AI_RATE_LIMIT,
            ErrorCode.SUMMARY_OPENAI_RATE_LIMIT,
            ErrorCode.SUMMARY_GEMINI_RATE_LIMIT
    );

    //REQUEST 계열 ERROR_CODE
    private static final Set<ErrorCode> REQUEST_ERROR_CODES = Set.of(
            ErrorCode.SUMMARY_AI_REQUEST_ERROR,
            ErrorCode.SUMMARY_OPENAI_REQUEST_ERROR,
            ErrorCode.SUMMARY_GEMINI_REQUEST_ERROR
    );

    //메시지 가져오기
    private static String resolveMessage(ErrorCode errorCode) {

        if (RATE_LIMIT_CODES.contains(errorCode)) {
            return ErrorMessage.SUMMARY_AI_RATE_LIMIT.getMessage();
        }

        if (REQUEST_ERROR_CODES.contains(errorCode)) {
            return ErrorMessage.SUMMARY_AI_REQUEST.getMessage();
        }

        ErrorMessage explicit = EXPLICIT_MAP.get(errorCode);
        return explicit != null ? explicit.getMessage() : null;
    }
}
