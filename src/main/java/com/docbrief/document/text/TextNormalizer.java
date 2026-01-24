package com.docbrief.document.text;

public final class TextNormalizer {

    // 특수문자 및 제어문자 처리
    // BOM 제거 : replace("\uFEFF", "")
    // 제어문자 제거 (탭/개행 제외) : replaceAll("[\\p{Cntrl}&&[^\n\t]]", "")
    // 특수 공백 정리 : replace("\u00A0", " ")
    // 여러 줄바꿈 정리 : replaceAll("\\n{3,}", "\n\n")
    private TextNormalizer() {}

    public static String normalizeForFullText(String text) {
        if (text == null) return "";
        return text.replace("\uFEFF", "")
                .replace("\u00A0", " ");
    }

    public static String normalizeForParagraph(String text) {
        if (text == null) return "";
        return text.replace("\uFEFF", "")
                .replace("\u00A0", " ")
                .replaceAll("\\n{3,}", "\n\n")
                .stripTrailing();       // 뒤에 붙은 공백만 제거
    }

    public static String normalizeForSentence(String text) {
        if (text == null) return "";
        return text.replace("\uFEFF", "")
                .replace("\u00A0", " ")
                .trim();
    }

}
