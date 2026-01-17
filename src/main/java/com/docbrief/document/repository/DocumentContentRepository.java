package com.docbrief.document.repository;

import com.docbrief.document.domain.DocumentContent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentContentRepository extends JpaRepository<DocumentContent, Long> {
    DocumentContent findByDocumentId(Long documentId);
    int deleteByDocumentId(Long documentId);
}
