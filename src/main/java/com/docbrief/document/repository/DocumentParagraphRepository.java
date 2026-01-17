package com.docbrief.document.repository;

import com.docbrief.document.domain.DocumentParagraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentParagraphRepository extends JpaRepository<DocumentParagraph, Long> {
    List<DocumentParagraph> findByDocumentIdOrderByParagraphOrder(Long documentId);
}
