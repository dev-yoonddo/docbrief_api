package com.docbrief.common;

import com.docbrief.document.domain.DocumentType;

public class UnsupportedDocumentTypeException extends BusinessException {

    public UnsupportedDocumentTypeException(DocumentType extension) {
        super(ErrorCode.UNSUPPORTED_DOCUMENT_TYPE, "지원하지 않는 문서 형식입니다. (extension:" + extension + ")");
    }

    public UnsupportedDocumentTypeException(String fileName) {
        super(ErrorCode.UNSUPPORTED_DOCUMENT_TYPE, "지원하지 않는 문서 형식입니다. (filename:" + fileName + ")");
    }
}
