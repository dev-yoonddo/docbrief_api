package com.docbrief.common;

public class DocumentResourceException extends BusinessException {

    public DocumentResourceException(String msg) {
        super(ErrorCode.DOCUMENT_RESOURCE_REQUIRED, msg);
    }
}
