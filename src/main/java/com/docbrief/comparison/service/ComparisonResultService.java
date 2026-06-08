package com.docbrief.comparison.service;

import com.docbrief.comparison.domain.ComparisonResult;
import com.docbrief.comparison.repository.ComparisonResultRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 비교분석 결과(ComparisonResult) 관리 서비스
 * - 비교분석 결과 저장 및 조회
 */
@Service
@AllArgsConstructor
@Transactional
public class ComparisonResultService {
    private final ComparisonResultRepository comparisonResultRepository;

    /**
     * 비교분석 결과를 저장한다.
     *
     * @param comparisonResult 저장할 비교분석 결과
     * @return 저장된 ComparisonResult
     */
    public ComparisonResult insertComparisonResult(ComparisonResult comparisonResult) {
        return comparisonResultRepository.save(comparisonResult);
    }

    /**
     * 작업 ID로 비교분석 결과를 조회한다.
     *
     * @param jobId 작업 ID
     * @return ComparisonResult Optional
     */
    public ComparisonResult getResult(Long jobId) {
        return comparisonResultRepository.findByComparisonJob_JobId(jobId)
                .orElseThrow(() -> new IllegalArgumentException("ComparisonResult not found"));
    }
}
