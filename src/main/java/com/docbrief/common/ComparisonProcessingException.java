package com.docbrief.common;

public class ComparisonProcessingException extends BusinessException {

    public ComparisonProcessingException() {
        super(ErrorCode.COMPARISON_PROCESSING_ERROR);
    }

    public ComparisonProcessingException(String customMessage) {
        super(ErrorCode.COMPARISON_PROCESSING_ERROR, customMessage);
    }
}
