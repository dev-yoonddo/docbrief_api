package com.docbrief.document.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DOCUMENT_CONTENTS")
public class DocumentContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CONTENT_ID")
    private Long id;

    @Column(name = "DOCUMENT_ID", nullable = false)
    private Long documentId;

    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    protected DocumentContent() {}

    public DocumentContent(Long documentId, String content) {
        this.documentId = documentId;
        this.content = content;
        this.createdAt = LocalDateTime.now();
    }
}

