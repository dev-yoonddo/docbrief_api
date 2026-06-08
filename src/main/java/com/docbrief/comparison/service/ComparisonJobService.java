package com.docbrief.comparison.service;

import com.docbrief.comparison.domain.ComparisonJob;
import com.docbrief.comparison.domain.ComparisonStatus;
import com.docbrief.comparison.repository.ComparisonJobRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 비교분석 작업(ComparisonJob) 관리 서비스
 * - 작업 상태 변경 처리
 */
@Service
@AllArgsConstructor
@Transactional
public class ComparisonJobService {
    private final ComparisonJobRepository comparisonJobRepository;

    /**
     * 새로운 비교분석 작업을 생성한다.
     *
     * @param jobId 작업 ID
     * @return 생성된 ComparisonJob
     */
    public ComparisonJob insertComparisonJob(Long jobId) {
        ComparisonJob job = new ComparisonJob(ComparisonStatus.CREATED);
        return comparisonJobRepository.save(job);
    }

    /**
     * 작업 ID로 비교분석 작업을 조회한다.
     *
     * @param jobId 작업 ID
     * @return ComparisonJob
     */
    private ComparisonJob getJob(Long jobId) {
        return comparisonJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("ComparisonJob not found"));
    }

    /**
     * 비교분석 작업 상태를 PROCESSING으로 변경한다.
     *
     * @param jobId 작업 ID
     * @return 업데이트된 ComparisonJob
     */
    public ComparisonJob setJobProcessing(Long jobId) {
        ComparisonJob job = getJob(jobId);
        job.updateStatus(ComparisonStatus.PROCESSING);
        return job;
    }

    /**
     * 비교분석 작업 상태를 COMPLETED로 변경한다.
     *
     * @param jobId 작업 ID
     * @return 업데이트된 ComparisonJob
     */
    public ComparisonJob setJobCompleted(Long jobId) {
        ComparisonJob job = getJob(jobId);
        job.updateStatus(ComparisonStatus.COMPLETED);
        return job;
    }

    /**
     * 비교분석 작업 상태를 FAILED로 변경한다.
     *
     * @param jobId 작업 ID
     * @return 업데이트된 ComparisonJob
     */
    public ComparisonJob setJobFailed(Long jobId) {
        ComparisonJob job = getJob(jobId);
        job.updateStatus(ComparisonStatus.FAILED);
        return job;
    }
}
