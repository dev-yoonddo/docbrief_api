package com.docbrief.comparison.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 문서 비교 결과를 저장하는 엔티티
 * - 비교 작업의 결과를 JSON 형식으로 저장
 * - 동의점, 차이점, 충돌점 및 전체 요약 정보 포함
 */
@Entity
@Table(name = "comparison_results")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ComparisonResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long resultId;

    /**
     * 비교 작업 참조 (FK)
     * - ComparisonJob과의 일대일 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private ComparisonJob comparisonJob;

    /**
     * 비교 결과 내용 (JSON 형식)
     * - 동의점, 차이점, 충돌점, 전체 요약, 규칙 위반 사유 포함
     * - JSON 구조:
     *   {
     *     "agreements": [...],
     *     "differences": [...],
     *     "conflicts": [...],
     *     "overallSummary": "...",
     *     "violationReason": "..."
     *   }
     */
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    /**
     * 결과 생성 시간
     */
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * 생성자
     *
     * @param comparisonJob 비교 작업
     * @param content JSON 형식의 비교 결과
     */
    public ComparisonResult(ComparisonJob comparisonJob, String content) {
        this.comparisonJob = comparisonJob;
        this.content = content;
    }
}
