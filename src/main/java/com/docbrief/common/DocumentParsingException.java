package com.docbrief.common;

public class DocumentParsingException extends BusinessException {

    public DocumentParsingException() {
        super(ErrorCode.DOC_PARSING_ERROR);
    }
}
