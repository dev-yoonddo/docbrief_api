package com.docbrief.summary.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "summary_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SummaryResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "result_id")
    private Long id;

    @Column(name = "job_id")
    private Long jobId;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;
}
