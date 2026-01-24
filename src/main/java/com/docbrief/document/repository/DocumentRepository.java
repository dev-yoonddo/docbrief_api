package com.docbrief.document.repository;

import com.docbrief.document.domain.Document;
import com.docbrief.document.domain.DocumentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.transaction.annotation.Transactional;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    Document findByDocumentId(Long documentId);

    @Transactional
    @Modifying
    @Query("update Document d set d.status = :status where d.documentId = :documentId and status != :status")
    int updateStatusByDocumentId(@NonNull Long documentId, @NonNull DocumentStatus status);
}
