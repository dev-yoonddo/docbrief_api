package com.docbrief.document.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long contentId;

    @Column(nullable = false)
    private Long documentId;

    @Column(nullable = false)
    private String fullText;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public static DocumentContent create(Long documentId, String fullText){
        DocumentContent documentContent = new DocumentContent();
        documentContent.documentId = documentId;
        documentContent.fullText = fullText;
        return documentContent;
    }
}


