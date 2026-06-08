package com.docbrief.comparison.domain;

import com.docbrief.document.domain.Document;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comparison_documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ComparisonDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private ComparisonJob comparisonJob;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "document_id", nullable = false)
    private Document document;

    @Column(name = "document_order", nullable = false)
    private int documentOrder;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public ComparisonDocument(ComparisonJob comparisonJob, Document document, int documentOrder) {
        this.comparisonJob = comparisonJob;
        this.document = document;
        this.documentOrder = documentOrder;
    }
}
