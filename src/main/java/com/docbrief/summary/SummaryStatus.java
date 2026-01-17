package com.docbrief.summary;

public enum SummaryStatus {
    REQUESTED,     // AI 분석 요청됨
    PROCESSING,    // AI 분석 중
    COMPLETED,     // 분석 결과 생성 완료
    DELIVERED,     // 사용자에게 응답 완료
    FAILED         // 분석 실패
}