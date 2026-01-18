package com.docbrief.common;

import com.docbrief.summary.domain.ErrorCode;
import lombok.Getter;

@Getter
public class SummaryProcessingException extends RuntimeException {
    private final ErrorCode errorCode;

    public SummaryProcessingException(
            ErrorCode errorCode,
            String message,
            Throwable cause
    ) {
        super(message, cause);
        this.errorCode = errorCode;
    }

}
