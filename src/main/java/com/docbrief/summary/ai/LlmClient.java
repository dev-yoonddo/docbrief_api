package com.docbrief.summary.ai;

import com.docbrief.comparison.domain.ComparisonJob;
import com.docbrief.summary.domain.SummaryJob;
import com.docbrief.summary.domain.SummaryRequest;
import com.docbrief.summary.domain.SummaryResult;

public interface LlmClient {
    /**
     * 문서 요약 수행
     */
    String summarize(String text, SummaryJob summaryJob);

    /**
     * 문서 비교분석 수행
     */
    String compare(String text, ComparisonJob comparisonJob);
}
