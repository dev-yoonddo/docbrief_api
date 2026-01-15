package com.docbrief.document.repository;

import com.docbrief.document.domain.DocumentSentence;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentSentenceRepository extends JpaRepository<DocumentSentence, Long> {
}
