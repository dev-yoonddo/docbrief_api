package com.docbrief.summary.service;

import com.docbrief.summary.domain.SummaryJob;
import com.docbrief.summary.domain.SummaryStatus;
import com.docbrief.summary.repository.SummaryJobRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Transactional
public class SummaryJobService {
    private final SummaryJobRepository summaryJobRepository;

    public SummaryJob insertSummaryJob(Long documentId){
        SummaryJob summaryJob = SummaryJob.create(documentId);
        return summaryJobRepository.save(summaryJob);
    }

    private SummaryJob getJob(Long jobId) {
        return summaryJobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("SummaryJob not found"));
    }

    public SummaryJob setJobProcessing(Long jobId) {
        SummaryJob job = getJob(jobId);
        job.start();
        return job;
    }

    public SummaryJob setJobCompleted(Long jobId) {
        SummaryJob job = getJob(jobId);
        job.complete();
        return job;
    }
    public SummaryJob setJobFailed(Long jobId) {
        SummaryJob job = getJob(jobId);
        job.fail();
        return job;
    }

}
