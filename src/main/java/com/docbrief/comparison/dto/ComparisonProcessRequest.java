package com.docbrief.comparison.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 문서 비교분석 처리 요청 DTO
 * - 두 문서의 ID와 비교 모드를 지정하여 비교분석 처리 요청
 */
@Getter
@Setter
@NoArgsConstructor
public class ComparisonProcessRequest {

    /**
     * 비교할 첫 번째 문서 ID (문서 A)
     */
    private Long documentAId;

    /**
     * 비교할 두 번째 문서 ID (문서 B)
     */
    private Long documentBId;

    /**
     * 비교 모드
     * - "full": 전체 내용 비교
     * - "section": 섹션별 비교
     */
    private String mode;

    /**
     * 생성자
     *
     * @param documentAId 문서 A ID
     * @param documentBId 문서 B ID
     * @param mode 비교 모드
     */
    public ComparisonProcessRequest(Long documentAId, Long documentBId, String mode) {
        this.documentAId = documentAId;
        this.documentBId = documentBId;
        this.mode = mode;
    }
}
