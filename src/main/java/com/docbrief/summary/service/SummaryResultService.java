package com.docbrief.summary.service;

import com.docbrief.summary.domain.SummaryJob;
import com.docbrief.summary.domain.SummaryResult;
import com.docbrief.summary.repository.SummaryJobRepository;
import com.docbrief.summary.repository.SummaryResultRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class SummaryResultService {
    private final SummaryResultRepository summaryResultRepository;

    public SummaryResult insertSummaryResult(SummaryResult summaryResult){
        return summaryResultRepository.save(summaryResult);
    }

    private SummaryResult getResult(Long jobId) {
        return summaryResultRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("SummaryResult not found"));
    }

}
