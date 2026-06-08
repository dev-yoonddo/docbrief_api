package com.docbrief.comparison.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 두 문서 간의 충돌점(모순되거나 상충되는 내용)을 나타내는 DTO
 * - 동의/차이와 달리, 충돌은 두 내용이 명시적으로 모순되는 경우
 */
@Getter
@Setter
@NoArgsConstructor
public class ConflictItem {

    /**
     * 충돌의 주제 또는 키
     * - 무엇이 상충하는지를 나타냄
     */
    private String topic;

    /**
     * 문서 A의 입장/주장
     */
    private String claimA;

    /**
     * 문서 B의 입장/주장
     */
    private String claimB;

    /**
     * 문서 A에서의 출처 정보
     */
    private Source sourceA;

    /**
     * 문서 B에서의 출처 정보
     */
    private Source sourceB;

    /**
     * 충돌의 심각도 또는 판단
     * - "high": 명확한 모순
     * - "medium": 상이한 입장
     * - "low": 미묘한 차이
     */
    private String severity;

    /**
     * 생성자
     *
     * @param topic 충돌의 주제
     * @param claimA 문서 A의 주장
     * @param claimB 문서 B의 주장
     * @param sourceA 문서 A의 출처 정보
     * @param sourceB 문서 B의 출처 정보
     * @param severity 충돌의 심각도
     */
    public ConflictItem(String topic, String claimA, String claimB, Source sourceA, Source sourceB, String severity) {
        this.topic = topic;
        this.claimA = claimA;
        this.claimB = claimB;
        this.sourceA = sourceA;
        this.sourceB = sourceB;
        this.severity = severity;
    }

    /**
     * 출처 정보 DTO
     * - 문서 내에서의 위치를 나타냄
     */
    @Getter
    @Setter
    @NoArgsConstructor
    public static class Source {

        /**
         * 문단 순서 (0부터 시작)
         */
        private int paragraphOrder;

        /**
         * 문장 순서 (0부터 시작)
         */
        private int sentenceOrder;

        /**
         * 생성자
         *
         * @param paragraphOrder 문단 순서
         * @param sentenceOrder 문장 순서
         */
        public Source(int paragraphOrder, int sentenceOrder) {
            this.paragraphOrder = paragraphOrder;
            this.sentenceOrder = sentenceOrder;
        }
    }
}
