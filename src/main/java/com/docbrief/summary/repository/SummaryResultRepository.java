package com.docbrief.summary.repository;

import com.docbrief.summary.domain.SummaryJob;
import com.docbrief.summary.domain.SummaryResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SummaryResultRepository extends JpaRepository<SummaryResult, Long> {

    Optional<SummaryResult> findByJobId(Long jobId);

}
