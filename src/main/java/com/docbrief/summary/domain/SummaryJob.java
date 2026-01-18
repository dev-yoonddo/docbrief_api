package com.docbrief.summary.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "summary_jobs", schema = "public")
@Getter
@Setter
@SequenceGenerator(
        name = "summary_job_seq_gen",
        sequenceName = "SUMMARY_JOB_SEQ",
        allocationSize = 1
)
public class SummaryJob {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "summary_job_seq_gen"
                    )
    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "document_id")
    private Long documentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 50)
    private SummaryStatus status;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "summary_type")
    private String summaryType;

    public SummaryJob() {}

    // 생성 책임을 엔티티로
    public static SummaryJob create(Long documentId) {
        SummaryJob job = new SummaryJob();
        job.documentId = documentId;
        job.status = SummaryStatus.REQUESTED;
        return job;
    }

    public void start() {
        if (status != SummaryStatus.REQUESTED) {
            throw new IllegalStateException("요청 상태가 아님");
        }
        this.status = SummaryStatus.PROCESSING;
    }

    public void complete() {
        this.status = SummaryStatus.COMPLETED;
    }

    public void fail() {
        this.status = SummaryStatus.FAILED;
    }
}
