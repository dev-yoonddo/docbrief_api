package com.docbrief.document.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="document_files")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="file_id")
    private Long fileId;

    @Column(name ="document_id", nullable=false)
    private Long documentId;

    @Column(name ="file_name",nullable=false)
    private String fileName;

    @Column(name="file_path",nullable=false)
    private String filePath;

    @Column(name ="file_size", nullable=false)
    private Long fileSize;

    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;

    public DocumentFile(Long documentId, String fileName, String filePath, Long fileSize) {
        this.documentId = documentId;
        this.fileName=fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
    }
}


