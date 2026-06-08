package com.docbrief.comparison.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 문서 비교 결과 응답 DTO
 * - 두 문서 간의 비교 분석 결과를 포함
 * - 동의점, 차이점, 충돌점 및 전체 요약 정보 제공
 */
@Getter
@Setter
@NoArgsConstructor
public class ComparisonResponse {

    /**
     * 두 문서가 동의하는 내용 목록
     */
    private List<AgreementItem> agreements;

    /**
     * 두 문서가 다른 내용 목록
     */
    private List<DifferenceItem> differences;

    /**
     * 두 문서가 상충하는 내용 목록
     */
    private List<ConflictItem> conflicts;

    /**
     * 전체 비교 결과 요약
     * - 전체 문맥에서 두 문서의 관계를 설명
     */
    private String overallSummary;

    /**
     * 규칙 위반 사유
     * - AI 출력이 지정된 규칙을 위반했을 경우의 설명
     * - null이면 규칙 위반 없음
     */
    private String violationReason;

    /**
     * 생성자
     *
     * @param agreements 동의점 목록
     * @param differences 차이점 목록
     * @param conflicts 충돌점 목록
     * @param overallSummary 전체 요약
     * @param violationReason 규칙 위반 사유
     */
    public ComparisonResponse(
            List<AgreementItem> agreements,
            List<DifferenceItem> differences,
            List<ConflictItem> conflicts,
            String overallSummary,
            String violationReason
    ) {
        this.agreements = agreements;
        this.differences = differences;
        this.conflicts = conflicts;
        this.overallSummary = overallSummary;
        this.violationReason = violationReason;
    }
}
