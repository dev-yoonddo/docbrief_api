package com.docbrief.document.repository;

import com.docbrief.document.domain.DocumentParagraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocumentParagraphRepository extends JpaRepository<DocumentParagraph, Long> {
}
