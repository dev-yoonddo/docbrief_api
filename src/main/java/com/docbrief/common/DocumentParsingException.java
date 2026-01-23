package com.docbrief.common;

public class DocumentParsingException extends RuntimeException {

    public DocumentParsingException() {
        super(ErrorCode.DOC_PARSING_ERROR.getMessage());
    }
}
