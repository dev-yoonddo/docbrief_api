package com.docbrief.document.repository;

import com.docbrief.document.domain.DocumentSentence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentSentenceRepository extends JpaRepository<DocumentSentence, Long> {
    List<DocumentSentence> findByParagraphIdOrderBySentenceOrder(Long paragraphId);
}
