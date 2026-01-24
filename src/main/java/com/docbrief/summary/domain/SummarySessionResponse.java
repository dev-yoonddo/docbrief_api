package com.docbrief.summary.domain;

import lombok.Getter;

import java.util.List;

@Getter
public class SummarySessionResponse {
    private String sessionId;
    private final Long jobId;
    private final SummaryResponse summaryResponse;

    // 생성자: sessionId 포함
    public SummarySessionResponse(String sessionId, SummarySessionResponse summarySessionResponse) {
        this.sessionId = sessionId;
        this.jobId = summarySessionResponse.getJobId();
        this.summaryResponse = summarySessionResponse.getSummaryResponse();
    }

    // 생성자: sessionId 없을 때
    public SummarySessionResponse(Long jobId, SummaryResponse summaryResponse) {
        this.jobId = jobId;
        this.summaryResponse = summaryResponse;
    }
}