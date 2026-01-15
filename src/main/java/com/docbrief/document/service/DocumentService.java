package com.docbrief.document.service;

import com.docbrief.document.domain.Document;
import com.docbrief.document.domain.DocumentStatus;
import com.docbrief.document.dto.DocumentCreateRequest;
import com.docbrief.document.repository.DocumentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class DocumentService {

    private final DocumentRepository documentRepository;

    public Long create(DocumentCreateRequest request){
        Document document = new Document(request.getTitle(), request.getDocumentType());
        documentRepository.save(document);
        return document.getDocumentId();
    }

    @Transactional(readOnly = true)
    public DocumentStatus getStatus(Long id){
        Document document = documentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("cannot find documentId"));
        return document.getStatus();
    }
}
