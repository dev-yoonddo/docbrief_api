package com.docbrief.common;

import com.docbrief.document.domain.DocumentStatus;
import lombok.Getter;

public class SummaryProcessingException extends BusinessException {

    private int statusCode;

    public SummaryProcessingException(ErrorCode errorCode) {
        super(errorCode);
    }

    public SummaryProcessingException(ErrorCode errorCode, int statusCode) {
        super(errorCode);
        this.statusCode = statusCode;
    }

    public SummaryProcessingException(ErrorCode errorCode, Throwable cause) {
        super(errorCode);
        initCause(cause);
    }

    public SummaryProcessingException(ErrorCode errorCode, Throwable cause, int statusCode) {
        super(errorCode);
        initCause(cause);
        this.statusCode = statusCode;
    }

    public SummaryProcessingException(ErrorCode errorCode, String message, Throwable cause, int statusCode) {
        super(errorCode, message);
        initCause(cause);
    }

    public int getStatusCode() {
        return statusCode;
    }
}
