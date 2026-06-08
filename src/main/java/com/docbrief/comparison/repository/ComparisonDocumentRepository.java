package com.docbrief.comparison.repository;

import com.docbrief.comparison.domain.ComparisonDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComparisonDocumentRepository extends JpaRepository<ComparisonDocument, Long> {
    List<ComparisonDocument> findByComparisonJobJobIdOrderByDocumentOrder(Long jobId);
}
