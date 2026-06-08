package com.docbrief.comparison.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "comparison_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ComparisonJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long jobId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ComparisonStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "comparisonJob", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ComparisonDocument> documents = new ArrayList<>();

    public ComparisonJob(ComparisonStatus status) {
        this.status = status;
    }

    public void updateStatus(ComparisonStatus status) {
        this.status = status;
    }
}
