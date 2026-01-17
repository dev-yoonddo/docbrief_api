package com.docbrief.document.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="document_contents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="content_id")
    private Long contentId;

    @Column(name= "document_id", nullable=false)
    private Long documentId;

    @Column(name= "full_text", nullable=false, columnDefinition="TEXT")
    private String fullText;

    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;

    public static DocumentContent create(Long documentId, String fullText){
        DocumentContent documentContent = new DocumentContent();
        documentContent.documentId = documentId;
        documentContent.fullText = fullText;
        return documentContent;
    }
}


