package com.docbrief.document.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name="document_paragraphs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DocumentParagraph {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="paragraph_id")
    private Long paragraphId;

    @Column(name="document_id", nullable=false)
    private Long documentId;

    @Column(name="paragraph_order", nullable=false)
    private int paragraphOrder;

    @CreationTimestamp
    @Column(name="created_at")
    private LocalDateTime createdAt;

    public static DocumentParagraph create(Long documentId, int order){
        DocumentParagraph documentParagraph = new DocumentParagraph();
        documentParagraph.documentId = documentId;
        documentParagraph.paragraphOrder = order;
        return documentParagraph;
    }
}

