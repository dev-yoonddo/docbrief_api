package com.docbrief.document.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="document_sentences")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentSentence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="sentence_id")
    private Long sentenceId;

    @Column(name="document_id", nullable=false)
    private Long documentId;

    @Column(name="paragraph_id")
    private Long paragraphId;

    @Column(name="sentence_order", nullable=false)
    private int sentenceOrder;

    @Column(name="sentence_text", nullable=false)
    private String sentenceText;

    @CreationTimestamp
    @Column(name="created_at")
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

