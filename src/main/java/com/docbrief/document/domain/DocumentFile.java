package com.docbrief.document.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "DOCUMENT_FILES")
public class DocumentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "FILE_ID")
    private Long id;

    @Column(name = "DOCUMENT_ID", nullable = false)
    private Long documentId;

    @Column(name = "FILE_NAME", nullable = false)
    private String fileName;

    @Column(name = "FILE_PATH", nullable = false)
    private String filePath;

    @Column(name = "FILE_SIZE", nullable = false)
    private Long fileSize;

    @Column(name = "CREATED_AT", updatable = false)
    private LocalDateTime createdAt;

    protected DocumentFile() {}

    public DocumentFile(Long documentId, String fileName, String filePath, Long fileSize) {
        this.documentId = documentId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.createdAt = LocalDateTime.now();
    }
}

