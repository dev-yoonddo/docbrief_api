package com.docbrief.comparison.dto;

import com.docbrief.comparison.domain.ComparisonStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 비교분석 작업 상태 조회 응답 DTO
 * - 비교분석 작업의 현재 상태와 생성/수정 시간 정보 포함
 */
@Getter
@NoArgsConstructor
public class ComparisonStatusResponse {

    /**
     * 비교분석 Job ID
     */
    private Long jobId;

    /**
     * 비교분석 작업 상태
     */
    private ComparisonStatus status;

    /**
     * 작업 생성 시간
     */
    private LocalDateTime createdAt;

    /**
     * 작업 수정 시간
     */
    private LocalDateTime updatedAt;

    /**
     * 생성자
     *
     * @param jobId 비교분석 Job ID
     * @param status 비교분석 작업 상태
     * @param createdAt 작업 생성 시간
     * @param updatedAt 작업 수정 시간
     */
    public ComparisonStatusResponse(Long jobId, ComparisonStatus status, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.jobId = jobId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
