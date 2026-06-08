package com.docbrief.comparison.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 두 문서 간의 동의점(동일한 내용)을 나타내는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class AgreementItem {

    /**
     * 동의점 내용
     */
    private String content;

    /**
     * 문서 A에서의 출처 정보
     */
    private Source sourceA;

    /**
     * 문서 B에서의 출처 정보
     */
    private Source sourceB;

    /**
     * 생성자
     *
     * @param content 동의점 내용
     * @param sourceA 문서 A의 출처 정보
     * @param sourceB 문서 B의 출처 정보
     */
    public AgreementItem(String content, Source sourceA, Source sourceB) {
        this.content = content;
        this.sourceA = sourceA;
        this.sourceB = sourceB;
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
