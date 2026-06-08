package com.docbrief.comparison.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 두 문서 간의 차이점(서로 다른 내용)을 나타내는 DTO
 */
@Getter
@Setter
@NoArgsConstructor
public class DifferenceItem {

    /**
     * 차이점 설명
     */
    private String description;

    /**
     * 차이점의 실제 텍스트
     * - 차이를 보이는 실제 텍스트나 두 내용을 대비하는 인용문
     */
    private String text;

    /**
     * 문서 A의 내용
     */
    private String contentA;

    /**
     * 문서 B의 내용
     */
    private String contentB;

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
     * @param description 차이점 설명
     * @param text 차이점의 실제 텍스트
     * @param contentA 문서 A의 내용
     * @param contentB 문서 B의 내용
     * @param sourceA 문서 A의 출처 정보
     * @param sourceB 문서 B의 출처 정보
     */
    public DifferenceItem(String description, String text, String contentA, String contentB, Source sourceA, Source sourceB) {
        this.description = description;
        this.text = text;
        this.contentA = contentA;
        this.contentB = contentB;
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
