package com.docbrief.document.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_sentences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentSentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sentenceId;

    @Column(nullable = false)
    private Long documentId;

    @Column
    private Long paragraphId; // nullable 핵심

    @Column(nullable = false)
    private int sentenceOrder;

    @Column(nullable = false)
    private String sentenceText;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public static DocumentSentence create(Long documentId, Long paragraphId, int order, String text) {
        DocumentSentence documentSentence = new DocumentSentence();
        documentSentence.documentId = documentId;
        documentSentence.paragraphId = paragraphId;
        documentSentence.sentenceOrder = order;
        documentSentence.sentenceText = text;
        return documentSentence;
    }
}

