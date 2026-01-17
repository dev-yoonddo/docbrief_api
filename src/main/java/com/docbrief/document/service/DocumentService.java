package com.docbrief.document.service;

import com.docbrief.document.domain.Document;
import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.dto.api.DocumentCreateRequest;
import com.docbrief.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {
    // 문서 생성, 업로드, 상태 관리

    private final DocumentRepository documentRepository;

    public Long create(DocumentCreateRequest request){
        Document document = new Document(request.getTitle(), request.getDocumentType());
        documentRepository.save(document);
        return document.getDocumentId();
    }

    @Transactional(readOnly = true)
    public DocumentStatus getStatus(Long documentId){
            Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("documentId for getStatus not found"));
        return document.getStatus();
    }

    @Transactional
    public Document updateStatus(Long documentId, DocumentStatus status){
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> new IllegalArgumentException("documentId for updateStatus not found"));
        document.updateStatus(status);
        return document;
    }

}
