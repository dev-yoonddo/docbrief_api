package com.docbrief.summary.domain;

import lombok.Getter;

@Getter
public class SummarySessionResponse {
    private String sessionId;
    private final Long jobId;
    private String title;
    private final SummaryResponse summaryResponse;

    // 생성자: sessionId 포함
    public SummarySessionResponse(String sessionId, String title, SummarySessionResponse summarySessionResponse) {
        this.sessionId = sessionId;
        this.jobId = summarySessionResponse.getJobId();
        this.title = title;
        this.summaryResponse = summarySessionResponse.getSummaryResponse();
    }

    // 생성자: sessionId 없을 때
    public SummarySessionResponse(Long jobId, String title, SummaryResponse summaryResponse) {
        this.jobId = jobId;
        this.title = title;
        this.summaryResponse = summaryResponse;
    }
}