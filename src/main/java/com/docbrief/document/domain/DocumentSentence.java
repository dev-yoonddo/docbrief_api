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

    @Lob
    @Column(nullable = false)
    private String text;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public DocumentSentence(
            Long documentId,
            Long paragraphId,
            int sentenceOrder,
            String text
    ) {
        this.documentId = documentId;
        this.paragraphId = paragraphId;
        this.sentenceOrder = sentenceOrder;
        this.text = text;
    }
}

