package com.docbrief.document.service;

import com.docbrief.document.domain.Document;
import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.repository.DocumentRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
public class DocumentStatusService {

    private final DocumentRepository documentRepository;

    // 호출 메서드에서 발생해도 상태값을 업데이트를 하기 위해 트랜잭션 분리
    @Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = {})
    public void updateDocumentStatus(Long documentId, DocumentStatus status) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("documentId for update status not found ::: documentId : " + documentId));
        document.updateStatus(status);
        documentRepository.flush();
    }
}
