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

    public static DocumentParagraph create(Long documentId, int order){
        DocumentParagraph documentParagraph = new DocumentParagraph();
        documentParagraph.documentId = documentId;
        documentParagraph.paragraphOrder = order;
        return documentParagraph;
    }
}

