package com.docbrief.summary.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "summary_results")
@Getter
@Setter
@NoArgsConstructor
@SequenceGenerator(
    name = "summary_job_seq_gen",
    sequenceName = "SUMMARY_JOB_SEQ",
    allocationSize = 1
)
public class SummaryResult {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
            generator = "summary_job_seq_gen"
    )
    @Column(name = "result_id")
    private Long id;

    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;


}
