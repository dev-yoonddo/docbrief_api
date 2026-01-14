package com.docbrief.document.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "document_paragraphs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentParagraph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paragraphId;

    @Column(nullable = false)
    private Long documentId;

    @Column(nullable = false)
    private int paragraphOrder;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public DocumentParagraph(Long documentId, int paragraphOrder) {
        this.documentId = documentId;
        this.paragraphOrder = paragraphOrder;
    }
}

