package com.docbrief.common;

public class ResourceNotFoundException extends BusinessException {

    public ResourceNotFoundException(Long documentId) {
        super(ErrorCode.DOCUMENT_NOT_FOUND, "문서를 찾을 수 없습니다. (" + documentId.toString() + ")");
    }
}
