package com.docbrief.common;

import com.docbrief.document.domain.DocumentStatus;
import lombok.Getter;

public class SummaryProcessingException extends BusinessException {

    public SummaryProcessingException(ErrorCode errorCode, Throwable cause) {
        super(errorCode);
        initCause(cause);
    }

    public SummaryProcessingException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode, message);
        initCause(cause);
    }
}
