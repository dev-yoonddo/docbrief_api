package com.docbrief.comparison.repository;

import com.docbrief.comparison.domain.ComparisonResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 비교 결과 저장소
 * - ComparisonResult 엔티티에 대한 데이터 접근 계층
 */
@Repository
public interface ComparisonResultRepository extends JpaRepository<ComparisonResult, Long> {

    /**
     * 비교 작업 ID로 비교 결과 조회
     *
     * @param jobId 비교 작업 ID
     * @return 비교 결과 Optional
     */
    Optional<ComparisonResult> findByComparisonJob_JobId(Long jobId);
}
