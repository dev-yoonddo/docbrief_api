package com.docbrief.document.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DOCUMENTS")
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "DOCUMENT_ID")
    private Long id;

    @Column(name = "TITLE", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "DOCUMENT_TYPE", nullable = false)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false)
    private DocumentStatus status;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    protected Document() {}

    public Document(String title, DocumentType documentType) {
        this.title = title;
        this.documentType = documentType;
        this.status = DocumentStatus.UPLOADED;
        this.createdAt = LocalDateTime.now();
    }

    public void markExtracted() {
        this.status = DocumentStatus.EXTRACTED;
    }

    public void markSummarized() {
        this.status = DocumentStatus.SUMMRAZIED;
    }

    public void markFailed() {
        this.status = DocumentStatus.FAILED;
    }
}
