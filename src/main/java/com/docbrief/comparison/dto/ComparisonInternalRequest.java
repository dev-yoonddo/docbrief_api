package com.docbrief.comparison.dto;

import com.docbrief.document.dto.internal.SummaryInternalRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 비교 처리를 위한 내부 요청 DTO
 * - 두 개의 문서 정보(SummaryInternalRequest)를 포함하는 래퍼 클래스
 * - 비교 엔진이 실제 문서 내용에 접근하기 위해 사용
 */
@Getter
@NoArgsConstructor
public class ComparisonInternalRequest {

    /**
     * 비교 모드
     * - "full": 전체 내용 비교
     * - "section": 섹션별 비교
     */
    private String mode;

    /**
     * 문서 A의 내부 요청 객체
     * - 문서 제목, 전체 텍스트, 문단-문장 구조 포함
     */
    private SummaryInternalRequest documentA;

    /**
     * 문서 B의 내부 요청 객체
     * - 문서 제목, 전체 텍스트, 문단-문장 구조 포함
     */
    private SummaryInternalRequest documentB;

    /**
     * 생성자
     *
     * @param mode 비교 모드
     * @param documentA 문서 A 정보
     * @param documentB 문서 B 정보
     */
    public ComparisonInternalRequest(String mode, SummaryInternalRequest documentA, SummaryInternalRequest documentB) {
        this.mode = mode;
        this.documentA = documentA;
        this.documentB = documentB;
    }
}
