package com.docbrief.summary.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Table(name = "summary_jobs", schema = "public")
@Getter
public class SummaryJob {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "document_id")
    private Long documentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private SummaryStatus status;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "summary_type")
    private String summaryType;
}
