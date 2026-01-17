package com.docbrief.document.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;

    @Column(name="title", nullable=false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name="document_type", nullable=false)
    private DocumentType documentType;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable=false)
    private DocumentStatus status;

    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;

    public Document(String title, DocumentType documentType) {
        this.title = title;
        this.documentType = documentType;
        this.status = DocumentStatus.UPLOADED;
    }

    public void updateStatus(DocumentStatus status){
        this.status = status;
    }
}

