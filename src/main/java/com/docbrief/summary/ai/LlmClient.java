package com.docbrief.summary.ai;

import com.docbrief.summary.domain.SummaryJob;
import com.docbrief.summary.domain.SummaryRequest;
import com.docbrief.summary.domain.SummaryResult;

public interface LlmClient {
    String summarize(String text, SummaryJob summaryJob);
}
