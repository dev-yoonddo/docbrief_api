package com.docbrief.common;

import com.docbrief.document.domain.DocumentStatus;

public class InvalidDocumentStatusException extends BusinessException{

    public InvalidDocumentStatusException(DocumentStatus status) {
        super(ErrorCode.INVALID_DOCUMENT_STATUS, "문서 상태가 올바르지 않습니다. 문서 상태 : " + status);
    }

    public InvalidDocumentStatusException(String msg) {
        super(ErrorCode.INVALID_DOCUMENT_STATUS, msg);
    }
}
